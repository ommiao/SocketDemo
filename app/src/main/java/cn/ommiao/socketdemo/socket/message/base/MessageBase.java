package cn.ommiao.socketdemo.socket.message.base;

import cn.ommiao.socketdemo.entity.JavaBean;

public class MessageBase extends JavaBean{

    private String MsgNo, Action, Body, ReplyTo;

    public String getMsgNo() {
        return MsgNo;
    }

    public void setMsgNo(String msgNo) {
        MsgNo = msgNo;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getReplyTo() {
        return ReplyTo;
    }

    public void setReplyTo(String replyTo) {
        ReplyTo = replyTo;
    }
}
