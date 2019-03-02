package cn.ommiao.socketdemo.socket.message.chat;

import cn.ommiao.socketdemo.socket.message.base.WrapperBody;
import cn.ommiao.socketdemo.socket.message.user.User;

public class MessageBody extends WrapperBody {

    private String Content;
    private User User;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        User = user;
    }
}
