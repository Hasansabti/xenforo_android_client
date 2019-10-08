package tech.sabtih.forumapp.models;

public class Threadreply {
    int id;
    String postid;
    String username;
    String text;
    int likes;

    public Threadreply(int id, String postid, String username, String text, int likes) {
        this.id = id;
        this.postid = postid;
        this.username = username;
        this.text = text;
        this.likes = likes;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
