package cn.ommiao.socketdemo.socket.client;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import cn.ommiao.socketdemo.socket.Action;
import cn.ommiao.socketdemo.socket.ErrorCodes;
import cn.ommiao.socketdemo.socket.call.SocketCall;
import cn.ommiao.socketdemo.socket.interfaces.SocketCallback;
import cn.ommiao.socketdemo.socket.message.MessageBase;
import cn.ommiao.socketdemo.socket.message.MessageWrapper;
import cn.ommiao.socketdemo.socket.message.WrapperBody;


public class LifeCycleManager {
    private Client client;
    private AtomicInteger heartBeatFailTimes = new AtomicInteger(0);
    private long firstDelay = 120000L;//
    private long HEARTBEAT_INTERVAL = 120000L;//heartbeat interval.
    private Handler messageCheckHandler;
    private MessageCheckTask messageCheckTask;

    private Timer heartBeatTimer;

    private HeartBeatTask heartBeatTask;

    public LifeCycleManager(Client client) {
        this.client = client;
    }

    public void start() {
        heartBeatTimer = new Timer(true);
        heartBeatTask = new HeartBeatTask();
        heartBeatTimer.schedule(heartBeatTask, firstDelay, HEARTBEAT_INTERVAL);
        messageCheckTask = new MessageCheckTask("MessageCheck");
        messageCheckTask.start();
        messageCheckHandler = new Handler(messageCheckTask.getLooper());
    }

    public void stop() {
        heartBeatTask.cancel();
        heartBeatTimer.purge();
        heartBeatTimer.cancel();
        messageCheckHandler.removeCallbacksAndMessages(null);
        messageCheckTask.quit();
    }

    public void enqueue(final SocketCall socketCall) {
        if (messageCheckTask.isAlive()){
            messageCheckHandler.postAtTime(new Runnable() {
                @Override
                public void run() {
                    client.onCallTimeOut(socketCall, ErrorCodes.ERROR_CODE_TIME_OUT);
                }
            }, socketCall, SystemClock.uptimeMillis() + socketCall.getTimeout());
        }
    }

    public void removeTimeOutCheck(final SocketCall socketCall) {
        messageCheckHandler.removeCallbacksAndMessages(socketCall);
    }

    private class HeartBeatTask extends TimerTask {
        @Override
        public void run() {
            MessageBase message = MessageBase.builder()
                    .create()
                    .action(Action.ACTION_HEART_BEAT)
                    .body(new WrapperBody())
                    .messageId(client.applyMessageId())
                    .build();
            SocketCall.builder(new HeartBeatWrapper())
                    .create()
                    .message(message)
                    .callback(new SocketCallback<HeartBeatWrapper>() {
                        @Override
                        public void onSuccess(Client client, HeartBeatWrapper wrapper, MessageBase received, MessageBase original) {
                            heartBeatFailTimes.set(0);
                        }

                        @Override
                        public void onFail(Client client, MessageBase original, int errorCode,String errorText) {
                            if (heartBeatFailTimes.incrementAndGet() > 3) {
                                client.onDisconnect();
                            }
                        }
                    })
                    .callbackInUiThread(false)
                    .build()
                    .call(client);
        }
    }

    private class HeartBeatWrapper extends MessageWrapper {

        public HeartBeatWrapper() {

        }

        @Override
        protected Class classOfT() {
            return WrapperBody.class;
        }
    }

    private class MessageCheckTask extends HandlerThread {

        public MessageCheckTask(String name) {
            super(name);
        }


    }
}
