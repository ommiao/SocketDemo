package cn.ommiao.socketdemo.socket.interfaces;


import cn.ommiao.socketdemo.call.SocketCall;
import cn.ommiao.socketdemo.socket.message.MessageWrapper;


public interface SocketClient<C extends SocketClient> {

    <T extends MessageWrapper> void call(SocketCall<T> socketCall);
    long applyMessageId();
    long timeToLastCommunication();
    void dispose();
}
