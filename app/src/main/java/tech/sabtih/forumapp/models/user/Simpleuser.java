package tech.sabtih.forumapp.models.user;

public class Simpleuser {
    int id;
    int staff;
    String name;
    String avatar;

    public Simpleuser(int id, String name, String avatar, int staff) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.staff = staff;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getStaff() {
        return staff;
    }

    public void setStaff(int staff) {
        this.staff = staff;
    }
}
