package cn.ommiao.socketdemo.socket.message.chat;

import cn.ommiao.socketdemo.socket.message.base.WrapperBody;

public class MessageBody extends WrapperBody {

    private String Content, Nickname;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }
}
