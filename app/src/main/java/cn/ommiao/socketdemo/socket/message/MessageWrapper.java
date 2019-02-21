package cn.ommiao.socketdemo.socket.message;

import cn.ommiao.socketdemo.utils.GsonUtil;

public abstract class MessageWrapper<T extends WrapperBody> {

    protected MessageBase message;
    protected transient T resolved;

    public MessageWrapper() {

    }

    public MessageWrapper(MessageBase message) {
        this.message = message;
    }

    protected abstract Class<T> classOfT();

    public T resolve(){
        if (resolved == null){
            resolved = GsonUtil.fromJson(message.getBody(),classOfT());
        }
        return resolved;
    }

    public MessageBase getMessage() {
        return message;
    }

    public void setMessage(MessageBase message) {
        this.message = message;
    }

    public String getAction(){
        return message.getAction();
    }

    public static MessageWrapper defaultWrapper(){
        return new MessageWrapper() {
            @Override
            protected Class classOfT() {
                return WrapperBody.class;
            }
        };
    }

}
