package tech.sabtih.forumapp.models;

import java.util.ArrayList;

public class Discussion {
    int id;
    String url;
    String title;
    String author;
    String cdate;

    ArrayList<Threadreply> treplies;

    public Discussion(int id, String url, String title, String author, String cdate, ArrayList<Threadreply> treplies) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.author = author;
        this.cdate = cdate;
        this.treplies = treplies;
    }


    public ArrayList<Threadreply> getTreplies() {
        return treplies;
    }

    public void setTreplies(ArrayList<Threadreply> treplies) {
        this.treplies = treplies;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }


}
