package cn.ommiao.socketdemo.socket.handler;


import java.util.concurrent.ConcurrentHashMap;

import cn.ommiao.socketdemo.call.SocketCall;
import cn.ommiao.socketdemo.socket.Action;
import cn.ommiao.socketdemo.socket.ErrorCodes;
import cn.ommiao.socketdemo.socket.client.Client;
import cn.ommiao.socketdemo.socket.message.MessageBase;
import cn.ommiao.socketdemo.socket.message.WrapperBody;


/**
 * description:
 * Created by WJD on 2018/1/20.
 */

public abstract class HandlerHub {
    private ConcurrentHashMap<String, BaseMessageHandler> handlers = new ConcurrentHashMap<>();

    public HandlerHub() {
        registerDefaultHandler();
    }

    public void handleMessage(Client client, MessageBase received) {
        BaseMessageHandler handler = handlers.get(received.getAction());
        if (handler != null) {
            handler.handleMessage(client, received);
        }else {
            MessageBase reply = MessageBase.builder()
                    .create()
                    .action(Action.ACTION_OPERATION_DENIED)
                    .body(new WrapperBody(ErrorCodes.ERROR_CODE_UNKNOWN_ACTION,"unknown action."))
                    .messageId(client.applyMessageId())
                    .replyTo(received.getMessageId())
                    .build();
            SocketCall.newOneWayCall(reply).call(client);
        }
    }

    protected abstract void registerDefaultHandler();

    public BaseMessageHandler register(BaseMessageHandler handler) {
            return handlers.put(handler.getAction(), handler);
    }

    public BaseMessageHandler unRegister(BaseMessageHandler handler) {
            return handlers.remove(handler.getAction());
    }

    public BaseMessageHandler unRegister(String action) {
            return handlers.remove(action);
    }
}
