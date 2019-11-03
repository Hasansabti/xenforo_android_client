package tech.sabtih.forumapp.models.user;

public class NameChange {
    String oldname;
    String newname;
    String date;

    public NameChange(String oldname, String newname, String date) {
        this.oldname = oldname;
        this.newname = newname;
        this.date = date;
    }


    public String getOldname() {
        return oldname;
    }

    public void setOldname(String oldname) {
        this.oldname = oldname;
    }

    public String getNewname() {
        return newname;
    }

    public void setNewname(String newname) {
        this.newname = newname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
