package cn.ommiao.socketdemo.socket.message.heartbeat;

import cn.ommiao.socketdemo.socket.message.base.AbstractMessageWrapper;
import cn.ommiao.socketdemo.socket.message.base.WrapperBody;

public class HeartBeatWrapper extends AbstractMessageWrapper<HeartBeatWrapper, WrapperBody> {

    public HeartBeatWrapper(String message) {
        super(message);
    }

    public HeartBeatWrapper() {
        super();
    }

    @Override
    public Class<WrapperBody> classOfT() {
        return WrapperBody.class;
    }
}
