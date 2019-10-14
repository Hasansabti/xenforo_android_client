package tech.sabtih.forumapp.models;

public class Chatmsg {
    int ID;
    String msgid;
    String date;
    Simpleuser user;
    String textcolor;
    String msg;
    boolean me;

    public Chatmsg(int ID, String msgid, String date, Simpleuser user, String textcolor, String msg) {
        this.ID = ID;
        this.msgid = msgid;
        this.date = date;
        this.user = user;
        this.textcolor = textcolor;
        this.msg = msg;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Simpleuser getUser() {
        return user;
    }

    public void setUser(Simpleuser user) {
        this.user = user;
    }

    public String getTextcolor() {
        return textcolor;
    }

    public void setTextcolor(String textcolor) {
        this.textcolor = textcolor;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isMe() {
        return me;
    }

    public void setMe(boolean me) {
        this.me = me;
    }
}
