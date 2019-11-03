package tech.sabtih.forumapp.models.user;

import java.util.ArrayList;

import tech.sabtih.forumapp.models.Profilepost;

public class User {
    private int ID;
    private String name;
    private String title;
    private String status;
    private String avatar;
    private String cover = "";
    private int points;
    private  int followers;
    private int following;
    private int messages;
    private ArrayList<UserRank> ranks;

    private ArrayList<Profilepost> profileposts;
    private UserInfo pinfo;
    private ArrayList<NameChange> nameChanges;
    private String lastActivity;

    public User(int ID, String name, String title, String status, int points, int followers, int following, int messages, String avatar) {
        this.ID = ID;
        this.name = name;
        this.title = title;
        this.status = status;
        this.points = points;
        this.followers = followers;
        this.following = following;
        this.messages = messages;
        this.avatar = avatar;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public ArrayList<Profilepost> getProfileposts() {
        return profileposts;
    }

    public void setProfileposts(ArrayList<Profilepost> profileposts) {
        this.profileposts = profileposts;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public ArrayList<UserRank> getRanks() {
        return ranks;
    }

    public void setRanks(ArrayList<UserRank> ranks) {
        this.ranks = ranks;
    }

    public UserInfo getPinfo() {
        return pinfo;
    }

    public void setPinfo(UserInfo pinfo) {
        this.pinfo = pinfo;
    }

    public ArrayList<NameChange> getNameChanges() {
        return nameChanges;
    }

    public void setNameChanges(ArrayList<NameChange> nameChanges) {
        this.nameChanges = nameChanges;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getLastActivity() {
        return lastActivity;
    }
}
