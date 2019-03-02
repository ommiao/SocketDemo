package cn.ommiao.socketdemo.socket.message.user;

import cn.ommiao.socketdemo.socket.message.base.AbstractMessageWrapper;

public class UserWrapper extends AbstractMessageWrapper<UserWrapper, UserBody> {

    public UserWrapper() {
        super();
    }

    public UserWrapper(String message) {
        super(message);
    }

    @Override
    public Class<UserBody> classOfT() {
        return UserBody.class;
    }
}
