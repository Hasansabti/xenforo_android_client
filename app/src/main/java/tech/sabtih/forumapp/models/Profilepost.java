package tech.sabtih.forumapp.models;

import java.util.ArrayList;

import tech.sabtih.forumapp.models.user.Simpleuser;

public class Profilepost {
    int id;
    int likes;
    Simpleuser user;
    String time;
    ArrayList<Profilepostmessage> replies;
    String message;
    private int numReplies;

    public Profilepost(int id, int likes, Simpleuser user, String time, String message) {
        this.id = id;
        this.likes = likes;
        this.user = user;
        this.time = time;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public Simpleuser getUser() {
        return user;
    }

    public void setUser(Simpleuser user) {
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<Profilepostmessage> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<Profilepostmessage> replies) {
        this.replies = replies;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setNumReplies(int numReplies) {
        this.numReplies = numReplies;
    }

    public int getNumReplies() {
        return numReplies;
    }
}
