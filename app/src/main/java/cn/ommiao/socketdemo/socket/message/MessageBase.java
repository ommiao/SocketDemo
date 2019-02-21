package cn.ommiao.socketdemo.socket.message;

import java.nio.charset.Charset;

import cn.ommiao.socketdemo.utils.GsonUtil;

public class MessageBase {

    public transient static final int MESSAGE_ID_INVALID = -1;
    private String body;
    private String action;
    private long messageId;
    private long replyTo = MESSAGE_ID_INVALID;
    private transient String json;
    private transient boolean needReJson = false;

    public static Builder builder(){
        return new Builder();
    }

    private MessageBase() {
    }

    public static MessageBase fromJson(String json) {
        return GsonUtil.fromJson(json, MessageBase.class);
    }

    public String getBody() {
        return body;
    }

    public String getAction() {
        return action;
    }

    public String getJson() {
        if (json == null || needReJson) {
            json = toJson();
        }
        return json;
    }

    public long getMessageId() {
        return messageId;
    }

    public long getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(long replyTo) {
        this.replyTo = replyTo;
        this.needReJson = true;
    }

    private String toJson() {
        return GsonUtil.toJson(this);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public byte[] toBytes(){
        String temp = getJson() + "\n";
        return temp.getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public String toString() {
        return "MessageBase{" +
                "body='" + body + '\'' +
                ", action='" + action + '\'' +
                ", messageId=" + messageId +
                ", replyTo=" + replyTo +
                ", json='" + json + '\'' +
                ", needReJson=" + needReJson +
                '}';
    }

    public static class Builder {
        private MessageBase message;

        public Builder() {

        }

        public ActionBuilder create() {
            message = new MessageBase();
            return new ActionBuilder(message);
        }

    }

    public static class ActionBuilder {
        private MessageBase message;

        ActionBuilder(MessageBase message) {
            this.message = message;
        }

        public BodyBuilder action(String action) {
            message.setAction(action);
            return new BodyBuilder(message);
        }

    }

    public static class BodyBuilder {
        private MessageBase message;

        BodyBuilder(MessageBase message) {
            this.message = message;
        }

        public MessageIdBuilder body(String body) {
            message.setBody(body);
            return new MessageIdBuilder(message);
        }

        public MessageIdBuilder body(Object body) {
            message.setBody(GsonUtil.toJson(body));
            return new MessageIdBuilder(message);
        }

    }

    public static class MessageIdBuilder {
        private MessageBase message;

        MessageIdBuilder(MessageBase message) {
            this.message = message;
        }

        public OptionalBuilder messageId(long messageId) {
            message.setMessageId(messageId);
            return new OptionalBuilder(message);
        }

    }

    public static class OptionalBuilder {
        private MessageBase message;

        OptionalBuilder(MessageBase message) {
            this.message = message;
        }

        public OptionalBuilder replyTo(long replyTo) {
            message.setReplyTo(replyTo);
            return this;
        }

        public MessageBase build() {
            return message;
        }

    }

}
