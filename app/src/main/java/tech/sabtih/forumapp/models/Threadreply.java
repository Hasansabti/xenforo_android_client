package tech.sabtih.forumapp.models;

import java.util.ArrayList;

public class Threadreply {
    int id;
    String postid;

    String text;
    int likes;
    String date;
    Simpleuser su;

    ArrayList<Threadreply> replies;


    public Threadreply(int id, String postid, String text, int likes, String date, Simpleuser su) {
        this.id = id;
        this.postid = postid;
        this.text = text;
        this.likes = likes;
        this.date = date;
        this.su = su;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Simpleuser getSu() {
        return su;
    }

    public void setSu(Simpleuser su) {
        this.su = su;
    }

    public ArrayList<Threadreply> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<Threadreply> replies) {
        this.replies = replies;
    }
}
