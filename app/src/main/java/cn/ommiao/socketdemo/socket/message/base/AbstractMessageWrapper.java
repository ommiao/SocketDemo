package cn.ommiao.socketdemo.socket.message.base;

public abstract class AbstractMessageWrapper<M extends AbstractMessageWrapper<M, T>, T extends WrapperBody> {

    private MessageBase message = new MessageBase();

    public AbstractMessageWrapper(){

    }

    public AbstractMessageWrapper(T body){
        message.setBody(body.toJson());
    }

    public AbstractMessageWrapper(String message){
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

    public void setBody(T body){
        message.setBody(body.toJson());
    }

    public abstract Class<T> classOfT();

}
