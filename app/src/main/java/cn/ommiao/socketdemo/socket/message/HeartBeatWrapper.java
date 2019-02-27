package cn.ommiao.socketdemo.socket.message;

public class HeartBeatWrapper extends MessageWrapper<WrapperBody> {


    @Override
    public Class<WrapperBody> classOfT() {
        return WrapperBody.class;
    }
}
