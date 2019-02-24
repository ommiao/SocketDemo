package cn.ommiao.socketdemo.socket.message;

public abstract class MessageWrapper<T extends WrapperBody> {

    private MessageBase message = new MessageBase();

    public MessageWrapper(){

    }

    public MessageWrapper(T body){
        message.setBody(body.toJson());
    }

    public MessageWrapper(String message){
        this.message = MessageBase.fromJson(message, MessageBase.class);
    }

    public String getMessageString(){
        return message.toJson();
    }

    public T getwrapperBody(){
        return T.fromJson(message.getBody(), classOfT());
    }

    public MessageWrapper<T> action(String action){
        message.setAction(action);
        return this;
    }

    public String getAction(){
        return message.getAction();
    }

    public abstract Class<T> classOfT();

}
