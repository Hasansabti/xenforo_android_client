package tech.sabtih.forumapp.models;

import tech.sabtih.forumapp.models.user.Simpleuser;

public class Profilepostmessage {
    int id;
    Simpleuser user;
    String time;
    String message;
    int likes;

    public Profilepostmessage(int id, Simpleuser user, String time, String message, int likes) {
        this.id = id;
        this.user = user;
        this.time = time;
        this.message = message;
        this.likes = likes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
