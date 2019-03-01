package cn.ommiao.socketdemo.socket.message.chat;

import cn.ommiao.socketdemo.socket.message.base.AbstractMessageWrapper;

public class MessageWrapper extends AbstractMessageWrapper<MessageWrapper, MessageBody> {

    public MessageWrapper() {
        super();
    }

    public MessageWrapper(String message) {
        super(message);
    }

    public MessageWrapper content(MessageBody body){
        setBody(body);
        return this;
    }

    public String getContent(){
        return getWrapperBody().getContent();
    }

    @Override
    public Class<MessageBody> classOfT() {
        return MessageBody.class;
    }

}
