package cn.ommiao.socketdemo.socket.message.chat;

import cn.ommiao.socketdemo.socket.message.base.WrapperBody;

public class MessageBody extends WrapperBody {

    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
