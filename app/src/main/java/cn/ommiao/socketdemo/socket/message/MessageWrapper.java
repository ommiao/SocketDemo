package cn.ommiao.socketdemo.socket.message;

public abstract class MessageWrapper<M extends MessageWrapper<M, T>, T extends WrapperBody> {

    private MessageBase message = new MessageBase();

    public MessageWrapper(){

    }

    public MessageWrapper(T body){
        message.setBody(body.toJson());
    }

    public MessageWrapper(String message){
        this.message = MessageBase.fromJson(message, MessageBase.class);
    }

    public String getStringMessage(){
        return message.toJson();
    }

    public T getWrapperBody(){
        return T.fromJson(message.getBody(), classOfT());
    }

    @SuppressWarnings("unchecked")
    public M action(String action){
        message.setAction(action);
        return (M)this;
    }

    public String getAction(){
        return message.getAction();
    }

    public abstract Class<T> classOfT();

}
