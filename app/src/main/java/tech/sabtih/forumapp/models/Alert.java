package tech.sabtih.forumapp.models;

import tech.sabtih.forumapp.models.user.Simpleuser;

public class Alert {
    private int alertid;
    private Simpleuser user;
    private String alerteext;
    private String date;
    private String link;

    public Alert(int alertid, Simpleuser user, String alerteext, String date) {
        this.alertid = alertid;
        this.user = user;
        this.alerteext = alerteext;
        this.date = date;
    }

    public int getAlertid() {
        return alertid;
    }

    public void setAlertid(int alertid) {
        this.alertid = alertid;
    }

    public Simpleuser getUser() {
        return user;
    }

    public void setUser(Simpleuser user) {
        this.user = user;
    }

    public String getAlertext() {
        return alerteext;
    }

    public void setAlerteext(String alerteext) {
        this.alerteext = alerteext;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAlerteext() {
        return alerteext;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
