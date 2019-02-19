package cn.ommiao.socketdemo.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class MessageEntity implements MultiItemEntity{

    public static final int TYPE_IN = 0;
    public static final int TYPE_OUT = 1;

    private String time, nickname, content;

    private int type;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
