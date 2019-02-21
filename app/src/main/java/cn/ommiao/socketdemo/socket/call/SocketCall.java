package cn.ommiao.socketdemo.socket.call;


import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import cn.ommiao.socketdemo.socket.ErrorCodes;
import cn.ommiao.socketdemo.socket.client.Client;
import cn.ommiao.socketdemo.socket.interfaces.SocketCallback;
import cn.ommiao.socketdemo.socket.message.MessageBase;
import cn.ommiao.socketdemo.socket.message.MessageWrapper;


/**
 * WJD 2018.1.19
 *
 * @param <T> type parameter for callback message Wrapper.
 */
public class SocketCall<T extends MessageWrapper> {

    public static final long DEFAULT_MESSAGE_TIME_OUT = 60_000L;
    private MessageBase message;
    private transient long callTime;
    private transient long timeout = DEFAULT_MESSAGE_TIME_OUT;
    private boolean needResponse = true;
    private boolean callbackInUiThread = true;
    private SocketCallback<T> callback;
    private T wrapper;
    private Handler handler = new Handler(Looper.getMainLooper());

    private SocketCall() {

    }

    public static <T extends MessageWrapper> Builder<T> builder(T wrapper) {
        return new Builder<T>(wrapper);
    }

    public boolean isNeedResponse() {
        return needResponse;
    }

    public void setNeedResponse(boolean needResponse) {
        this.needResponse = needResponse;
    }

    public MessageBase getMessage() {
        return message;
    }

    public void setMessage(MessageBase message) {
        this.message = message;
    }

    public long getCallTime() {
        return callTime;
    }

    public void setCallTime(long sendTime) {
        this.callTime = sendTime;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setCallback(SocketCallback<T> callback) {
        this.callback = callback;
    }

    public void setWrapper(T wrapper) {
        this.wrapper = wrapper;
    }

    public SocketCall<T> call(Client client) {
        client.call(this);
        return this;
    }

    public static SocketCall<MessageWrapper> newOneWayCall(MessageBase message) {
        return builder(MessageWrapper.defaultWrapper())
                .create()
                .message(message)
                .callback(new SocketCallback<MessageWrapper>() {
                    @Override
                    public void onSuccess(Client client, MessageWrapper wrapper, MessageBase received, MessageBase original) {

                    }

                    @Override
                    public void onFail(Client client, MessageBase original, int errorCode,String errorText) {

                    }
                })
                .needResponse(false)
                .callbackInUiThread(false)
                .build();
    }

    @Override
    public String toString() {
        return "SocketCall{" +
                "message=" + message +
                ", callTime=" + callTime +
                ", timeout=" + timeout +
                ", needResponse=" + needResponse +
                '}';
    }

    public void onSuccess(final Client client, final MessageBase received, final MessageBase original) {
        if (callback != null) {
            wrapper.setMessage(received);
            if (!needResponse || ErrorCodes.ERROR_CODE_OK == wrapper.resolve().getErrorcode()) {
                if (callbackInUiThread) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(client, wrapper, received, original);
                        }
                    });
                } else {
                    callback.onSuccess(client, wrapper, received, original);
                }
            } else {
                if (callbackInUiThread) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFail(client, original,wrapper.resolve().getErrorcode(),wrapper.resolve().getErrortext());
                        }
                    });
                } else {
                    callback.onFail(client, original,wrapper.resolve().getErrorcode(),wrapper.resolve().getErrortext());
                }
            }
        }
    }

    public void onFail(final Client client, final MessageBase original, final int errorCode) {
        if (callback != null) {
            if (callbackInUiThread) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail(client, original, errorCode,"网络错误");
                    }
                });
            } else {
                callback.onFail(client, original, errorCode,"网络错误");
            }
        }
    }

    /**
     * builder
     *
     * @param <T>
     */
    public static class Builder<T extends MessageWrapper> {
        private SocketCall<T> call;
        private T wrapper;

        Builder(T wrapper) {
            this.wrapper = wrapper;
        }

        public MessageBuilder<T> create() {
            call = new SocketCall<>();
            call.setWrapper(wrapper);
            return new MessageBuilder<>(call);
        }

    }

    /**
     * builder to set message.
     *
     * @param <T>
     */
    public static class MessageBuilder<T extends MessageWrapper> {
        private SocketCall<T> call;

        MessageBuilder(SocketCall<T> call) {
            this.call = call;
        }

        public ListenerBuilder<T> message(@NonNull MessageBase message) {
            call.setMessage(message);
            //default reply message need no response.
            if(message.getReplyTo() != MessageBase.MESSAGE_ID_INVALID){
                call.setNeedResponse(false);
            }
            return new ListenerBuilder<>(call);
        }
    }

    public static class ListenerBuilder<T extends MessageWrapper> {
        private SocketCall<T> call;

        ListenerBuilder(SocketCall<T> call) {
            this.call = call;
        }

        public MassBuilder<T> callback(SocketCallback<T> callback) {
            call.setCallback(callback);
            return new MassBuilder<>(call);
        }
    }

    public static class MassBuilder<T extends MessageWrapper> {
        private SocketCall<T> call;

        MassBuilder(SocketCall<T> call) {
            this.call = call;
        }

        public MassBuilder<T> timeout(long timeout) {
            call.setTimeout(timeout);
            return this;
        }

        public MassBuilder<T> needResponse(boolean needResponse) {
            call.setNeedResponse(needResponse);
            return this;
        }

        public MassBuilder<T> callbackInUiThread(boolean callbackInUiThread) {
            call.callbackInUiThread = callbackInUiThread;
            return this;
        }

        public SocketCall<T> build() {
            return call;
        }
    }
}
