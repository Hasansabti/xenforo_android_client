package tech.sabtih.forumapp.models;

public class Newsitem {
    private int id;
    private String date;
    private String title;
    private boolean sticky;
    private String maker;
    private int comments_num;
    private int views;
    private int likes;

    private String text;

    public Newsitem(int id, String date, String title, boolean sticky, String maker, int comments_num, String text, int views, int likes) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.sticky = sticky;
        this.maker = maker;
        this.comments_num = comments_num;
        this.text = text;
        this.views = views;
        this.likes = likes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public int getComments_num() {
        return comments_num;
    }

    public void setComments_num(int comments_num) {
        this.comments_num = comments_num;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
