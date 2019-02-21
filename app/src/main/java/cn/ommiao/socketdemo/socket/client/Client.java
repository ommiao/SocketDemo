package cn.ommiao.socketdemo.socket.client;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import cn.ommiao.socketdemo.socket.ErrorCodes;
import cn.ommiao.socketdemo.socket.call.SocketCall;
import cn.ommiao.socketdemo.socket.handler.HandlerHub;
import cn.ommiao.socketdemo.socket.message.MessageBase;
import cn.ommiao.socketdemo.socket.message.MessageWrapper;

public class Client {

    private static final String TAG = "Client";
    private final Socket socket;
    private final LinkedHashMap<Long, SocketCall> sendQueue = new LinkedHashMap<>();
    private final Object lock = new Object();
    private final Object writeLock = new Object();
    private volatile long lastCommunicationTime = System.currentTimeMillis();
    private ExecutorService EXECUTOR;
    private AtomicLong messageIdDispatcher = new AtomicLong(System.currentTimeMillis());
    private LifeCycleManager lifeCycleManager;
    private ConnectionManager connectionManager;
    private volatile boolean disposed = false;
    private HandlerHub handlerHub;
    private volatile boolean abandonCallBack = false;

    Client(Socket socket, ConnectionManager connectionManager, HandlerHub handlerHub, ExecutorService EXECUTOR) {
        this.socket = socket;
        this.EXECUTOR = EXECUTOR;
        this.connectionManager = connectionManager;
        this.handlerHub = handlerHub;
        startLifeCycleManager();
        startMaintainTask();
    }

    private void startLifeCycleManager() {
        lifeCycleManager = new LifeCycleManager(this);
        lifeCycleManager.start();
    }

    private void startMaintainTask() {
        EXECUTOR.execute(new MaintainTask(socket));
    }

    void onDisconnect() {
        if (connectionManager.getStatus() == ConnectionStatus.Connected) {
            synchronized (lock) {
                if (connectionManager.getStatus() == ConnectionStatus.Connected) {
                    dispose();
                    connectionManager.onDisconnected();
                }
            }
        }

    }

    public <T extends MessageWrapper> void call(SocketCall<T> socketCall) {
        socketCall.setCallTime(System.currentTimeMillis());
        //if socket is closed.
        if (!socket.isConnected() || socket.isClosed() || socket.isOutputShutdown()) {
            socketCall.onFail(this, socketCall.getMessage(), ErrorCodes.ERROR_CODE_SOCKET_CLOSED);
            return;
        }
        write(socket, socketCall);
    }

    /**
     * dispose this object to make gc collect it.
     */
    public void dispose() {
        if (disposed) return;
        synchronized (lock) {
            if (!disposed) {
                disposed = true;
                lifeCycleManager.stop();
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!sendQueue.isEmpty()) {
                    Set<Long> keys = sendQueue.keySet();
                    for (Long messageId : keys) {
                        SocketCall call = sendQueue.get(messageId);
                        if (!abandonCallBack && call != null) {
                            call.onFail(this, call.getMessage(), ErrorCodes.ERROR_CODE_SOCKET_CLOSED);
                        }
                    }
                }
            }
        }
    }

    public void disconnect() {
        abandonCallBack = true;
        dispose();
    }

    /**
     * write date to socket object.
     */
    private void write(final Socket socket, final SocketCall call) {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                writeSynchronized(socket, call.getMessage());
                if (!call.isNeedResponse()) {
                    call.onSuccess(Client.this, null, call.getMessage());
                } else {
                    synchronized (lock) {
                        sendQueue.put(call.getMessage().getMessageId(), call);
                    }
                    lifeCycleManager.enqueue(call);
                }
            }
        });

    }

    /**
     * write to client synchronize
     */
    private void writeSynchronized(final Socket socket, final MessageBase msg) {
        try {
            synchronized (writeLock) {
                OutputStream os = socket.getOutputStream();
                os.write(msg.toBytes());
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * time interval from last communication.
     */
    public long timeToLastCommunication() {
        return System.currentTimeMillis() - lastCommunicationTime;
    }

    /**
     * when call succeed.
     * this is called from read thread.
     */
    private void checkSuccessCall(final MessageBase received) {
        long resultOfMessageId;
        if (received != null && (resultOfMessageId = received.getReplyTo()) != MessageBase.MESSAGE_ID_INVALID) {
            SocketCall fromQueue;
            synchronized (lock) {
                fromQueue = sendQueue.remove(resultOfMessageId);
            }
            //if queue is not removed from queue.
            if (fromQueue != null) {
                //schedule this task to another Thread to avoid blocking reading thread.
                lifeCycleManager.removeTimeOutCheck(fromQueue);
                final SocketCall finalFromQueue = fromQueue;
                EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "call cost time : " + (System.currentTimeMillis() - finalFromQueue.getCallTime()));
                        finalFromQueue.onSuccess(Client.this, received, finalFromQueue.getMessage());
                    }
                });
            }
        }

    }

    /**
     * when call succeed.
     * this is called from read thread.
     */
    <T extends MessageWrapper> void onCallTimeOut(final SocketCall<T> call, final int errorCode) {
        SocketCall fromQueue;
        synchronized (lock) {
            fromQueue = sendQueue.remove(call.getMessage().getMessageId());

            //if queue is not removed from queue.
            if (fromQueue != null) {
                //schedule this task to another Thread to avoid blocking reading thread.
                EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        call.onFail(Client.this, call.getMessage(), errorCode);
                    }
                });
            }
        }

    }

    public long applyMessageId() {
        return messageIdDispatcher.incrementAndGet();
    }

    public Socket getSocket() {
        return socket;
    }

    private class MaintainTask implements Runnable {
        private Socket socket;

        MaintainTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("MaintainThread started.");
            InputStream stream;
            try {
                stream = socket.getInputStream();
                //request client to auth.
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(stream));
                String read;
                while ((read = reader.readLine()) != null) {
                    if (read.length() > 2) {
                        lastCommunicationTime = System.currentTimeMillis();
                        final MessageBase received = MessageBase.fromJson(read);
                        EXECUTOR.execute(new Runnable() {
                            @Override
                            public void run() {
                                if (handlerHub != null && received.getReplyTo() == MessageBase.MESSAGE_ID_INVALID) {
                                    //if this message is not a response for a older message.
                                    handlerHub.handleMessage(Client.this, received);
                                } else {
                                    //if this message is a response for a older message.
                                    checkSuccessCall(received);
                                }
                            }
                        });
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                onDisconnect();
            }

        }

    }

}
