package cn.ommiao.socketdemo.socket.interfaces;


import cn.ommiao.socketdemo.socket.client.Client;
import cn.ommiao.socketdemo.socket.message.MessageBase;
import cn.ommiao.socketdemo.socket.message.MessageWrapper;

public interface SocketCallback<T extends MessageWrapper> {
    void onSuccess(Client client, T wrapper, MessageBase received, MessageBase original);
    void onFail(Client client, MessageBase original, int errorCode, String errorText);
}
