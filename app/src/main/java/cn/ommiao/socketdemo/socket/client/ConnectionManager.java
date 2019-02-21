package cn.ommiao.socketdemo.socket.client;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.ommiao.socketdemo.socket.Config;
import cn.ommiao.socketdemo.socket.ErrorCodes;
import cn.ommiao.socketdemo.socket.client.handler.ClientMessageHandlerHub;
import cn.ommiao.socketdemo.socket.handler.BaseMessageHandler;
import cn.ommiao.socketdemo.socket.handler.HandlerHub;

import static cn.ommiao.socketdemo.socket.Config.RECONNECT_TRY_TIMES;

public class ConnectionManager {

    private final Object lock = new Object();
    private ConnectionStatus status = ConnectionStatus.NotConnected;
    private int retryTimes = 0;
    private ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private StatusReporter reporter;
    private volatile Client client;
    private HandlerHub handlerHub;
    private volatile boolean abandoned = false;

    private ConnectionManager() throws Exception {
        handlerHub = new ClientMessageHandlerHub();
    }

    public static ConnectionManager newConnection() throws Exception {
        return new ConnectionManager();
    }

    public void connect(OnSocketStatusListener listener) {
        retryTimes = 0;
        reporter = new StatusReporter(listener);
        connect();
    }

    private void connect() {
        synchronized (lock) {
            EXECUTOR.execute(() -> {
                synchronized (lock) {
                    try {
                        Socket socket = new Socket(Config.IP, Config.port);
                        if (socket.isConnected()) {
                            client = new Client(socket, ConnectionManager.this, handlerHub, EXECUTOR);
                        } else {
                            if (status == ConnectionStatus.Disconnected) {
                                onRetryFail();
                            } else {
                                reporter.onConnectFail(ErrorCodes.ERROR_CODE_FAIL, "connect fail");
                            }
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        if (status == ConnectionStatus.Disconnected) {
                            onRetryFail();
                        } else {
                            reporter.onConnectFail(ErrorCodes.ERROR_CODE_FAIL, "connect fail");
                        }
                    }
                }

            });
        }

    }

    public void disconnect() {
        EXECUTOR.execute(() -> {
            abandoned = true;
            if (client != null) {
                client.disconnect();
            }
        });
    }

    public void onDisconnected() {
        synchronized (lock) {
            if (client != null) {
                client = null;
            }
        }
        reporter.onDisconnected();
        reConnect();
    }

    private void reConnect() {
        if (abandoned) return;
        synchronized (lock) {
            if (client != null) {
                client = null;
            }
            retryTimes++;
        }
        reporter.onReconnecting(retryTimes);
        connect();
    }

    private void onRetryFail() {
        if (retryTimes >= RECONNECT_TRY_TIMES) {
            reporter.onReconnectFail();
        } else {
            reConnect();
        }
    }

    public ConnectionStatus getStatus() {
        synchronized (lock) {
            return status;
        }
    }

    public void setStatus(ConnectionStatus status) {
        synchronized (lock) {
            this.status = status;
        }
    }

    public HandlerHub getHandlerHub() {
        return handlerHub;
    }

    public void register(BaseMessageHandler handler) {
        handlerHub.register(handler);
    }

    public void unRegister(BaseMessageHandler handler) {
        handlerHub.unRegister(handler);
    }

    /**
     * all method are called from UI Thread.
     */
    public interface OnSocketStatusListener {
        void onConnected(Client client);

        void onConnectFail(int errorCode, String error);

        void onReconnecting(int retryTimes);

        void onDisconnected();

        void onReconnected(Client client);

        void onReconnectFail();
    }

    private class StatusReporter implements OnSocketStatusListener {
        private OnSocketStatusListener statusListener;

        private Handler handler;

        StatusReporter(OnSocketStatusListener statusListener) {
            if (statusListener == null)
                throw new NullPointerException("statusListener can't be null.");
            this.statusListener = statusListener;
            handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void onConnected(final Client client) {
            synchronized (lock) {
                setStatus(ConnectionStatus.Connected);
            }
            handler.post(() -> statusListener.onConnected(client));
        }

        @Override
        public void onConnectFail(final int errorCode, final String error) {
            synchronized (lock) {
                setStatus(ConnectionStatus.NotConnected);
            }
            handler.post(() -> statusListener.onConnectFail(errorCode, error));

        }

        @Override
        public void onReconnecting(final int retryTimes) {
            handler.post(() -> statusListener.onReconnecting(retryTimes));
        }

        @Override
        public void onDisconnected() {
            synchronized (lock) {
                setStatus(ConnectionStatus.Disconnected);
            }
            handler.post(() -> statusListener.onDisconnected());
        }

        @Override
        public void onReconnected(final Client client) {
            synchronized (lock) {
                setStatus(ConnectionStatus.Connected);
            }
            handler.post(() -> statusListener.onReconnected(client));
        }

        @Override
        public void onReconnectFail() {
            synchronized (lock) {
                setStatus(ConnectionStatus.Disconnected);
            }
            handler.post(() -> statusListener.onReconnectFail());
        }
    }
}
