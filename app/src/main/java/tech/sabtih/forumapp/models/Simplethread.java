package tech.sabtih.forumapp.models;

import tech.sabtih.forumapp.models.user.Simpleuser;

public class Simplethread {
    private int ID;
    private String title;
    private Simpleuser maker;
    private String latestreplydate;
    private String startdate;
    private String lastreplyname;
    private String makeravatar;
    private int replies;
    private int views;
    private boolean isread;
    private boolean isticky;
    private int pages = 1;

    public Simplethread(int ID, String title, Simpleuser maker, String latestreplydate, String startdate, String lastreplyname, String makeravatar, int replies, int views, boolean isread, boolean isticky) {
        this.ID = ID;
        this.title = title;
        this.maker = maker;
        this.latestreplydate = latestreplydate;
        this.startdate = startdate;
        this.lastreplyname = lastreplyname;
        this.makeravatar = makeravatar;
        this.replies = replies;
        this.views = views;
        this.isread = isread;
        this.isticky = isticky;
    }

    public boolean isIsread() {
        return isread;
    }

    public void setIsread(boolean isread) {
        this.isread = isread;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Simpleuser getMaker() {
        return maker;
    }

    public void setMaker(Simpleuser maker) {
        this.maker = maker;
    }

    public String getLatestreplydate() {
        return latestreplydate;
    }

    public void setLatestreplydate(String latestreplydate) {
        this.latestreplydate = latestreplydate;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getLastreplyname() {
        return lastreplyname;
    }

    public void setLastreplyname(String lastreplyname) {
        this.lastreplyname = lastreplyname;
    }

    public String getMakeravatar() {
        return makeravatar;
    }

    public void setMakeravatar(String makeravatar) {
        this.makeravatar = makeravatar;
    }

    public int getReplies() {
        return replies;
    }

    public void setReplies(int replies) {
        this.replies = replies;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public boolean isSticky() {
        return isticky;
    }

    public void setIsticky(boolean isticky) {
        this.isticky = isticky;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
