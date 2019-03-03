package cn.ommiao.socketdemo.socket.message.heartbeat;

import cn.ommiao.socketdemo.socket.message.base.AbstractMessageWrapper;

public class HeartBeatWrapper extends AbstractMessageWrapper<HeartBeatWrapper, HeartBeatBody> {

    public HeartBeatWrapper(String message) {
        super(message);
    }

    public HeartBeatWrapper() {
        super();
    }

    @Override
    public Class<HeartBeatBody> classOfT() {
        return HeartBeatBody.class;
    }
}
