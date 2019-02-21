package cn.ommiao.socketdemo.socket.handler;


import cn.ommiao.socketdemo.socket.client.Client;
import cn.ommiao.socketdemo.socket.message.MessageBase;


public abstract class BaseMessageHandler {

    public BaseMessageHandler() {

    }

    /**
     * handle the message.
     * @param client the Socket Client associated to this handler.
     * @param received the Message to handle.
     */
    public abstract void handleMessage(Client client, MessageBase received);

    /**
     * this action must be unique.
     * @return the action this handler is going to handle.
     */
    public abstract String getAction();
}
