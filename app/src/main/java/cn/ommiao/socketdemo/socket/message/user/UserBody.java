package cn.ommiao.socketdemo.socket.message.user;

import java.util.ArrayList;

import cn.ommiao.socketdemo.socket.message.base.WrapperBody;

public class UserBody extends WrapperBody {

    private String Event;

    private ArrayList<User> CurrentUsers;

    private User ChangedUser;

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public ArrayList<User> getCurrentUsers() {
        return CurrentUsers;
    }

    public User getChangedUser() {
        return ChangedUser;
    }

    public void setChangedUser(User changedUser) {
        ChangedUser = changedUser;
    }

    public boolean isUserAdded(){
        return EventDefine.EVENT_USER_IN.equals(Event);
    }

    public boolean isUserQuited(){
        return EventDefine.EVENT_USER_OUT.equals(Event);
    }

    public boolean isLogonSuccess(){
        return EventDefine.EVENT_USER_LOGON_SUCCESS.equals(Event);
    }

    public boolean isLogoutSuccess(){
        return EventDefine.EVENT_USER_LOGOUT_SUCCESS.equals(Event);
    }
}
