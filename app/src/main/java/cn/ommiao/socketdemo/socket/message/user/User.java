package cn.ommiao.socketdemo.socket.message.user;

import cn.ommiao.socketdemo.entity.JavaBean;

public class User extends JavaBean {

    private String UserCode, Nickname;

    public String getUserCode() {
        return UserCode;
    }

    public void setUserCode(String userCode) {
        UserCode = userCode;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }
}
