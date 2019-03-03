package cn.ommiao.socketdemo.socket.message.heartbeat;

import cn.ommiao.socketdemo.socket.message.base.WrapperBody;

public class HeartBeatBody extends WrapperBody {

    private String UserCode;

    public String getUserCode() {
        return UserCode;
    }

    public void setUserCode(String userCode) {
        UserCode = userCode;
    }
}
