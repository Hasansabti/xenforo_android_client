package tech.sabtih.forumapp.models;

public class Replyform {

    private String url;
    private String msg;
    private String lastdate;
    private String lastkdate;
    private String atthash;
    private String rresolver;

    public Replyform(String url, String msg, String lastdate, String lastkdate, String atthash, String rresolver) {
        this.url = url;
        this.msg = msg;
        this.lastdate = lastdate;
        this.lastkdate = lastkdate;
        this.atthash = atthash;
        this.rresolver = rresolver;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getLastdate() {
        return lastdate;
    }

    public void setLastdate(String lastdate) {
        this.lastdate = lastdate;
    }

    public String getLastkdate() {
        return lastkdate;
    }

    public void setLastkdate(String lastkdate) {
        this.lastkdate = lastkdate;
    }

    public String getAtthash() {
        return atthash;
    }

    public void setAtthash(String atthash) {
        this.atthash = atthash;
    }

    public String getRresolver() {
        return rresolver;
    }

    public void setRresolver(String rresolver) {
        this.rresolver = rresolver;
    }

    @Override
    public String toString() {
        return "Replyform{" +
                "url='" + url + '\'' +
                ", msg='" + msg + '\'' +
                ", lastdate='" + lastdate + '\'' +
                ", lastkdate='" + lastkdate + '\'' +
                ", atthash='" + atthash + '\'' +
                ", rresolver='" + rresolver + '\'' +
                '}';
    }
}
