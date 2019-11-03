package tech.sabtih.forumapp.models.user;

public class UserRank {
    int id;
    String title;
    String color;

    public UserRank(int id, String title, String color) {
        this.id = id;
        this.title = title;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
