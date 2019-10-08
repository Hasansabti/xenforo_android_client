package tech.sabtih.forumapp.models;

public class User {
    private int ID;
    private String name;
    private String title;
    private String status;
    private int points;
    private  int followers;
    private int following;
    private int messages;

    public User(int ID, String name, String title, String status, int points, int followers, int following, int messages) {
        this.ID = ID;
        this.name = name;
        this.title = title;
        this.status = status;
        this.points = points;
        this.followers = followers;
        this.following = following;
        this.messages = messages;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getMessages() {
        return messages;
    }

    public void setMessages(int messages) {
        this.messages = messages;
    }
}
