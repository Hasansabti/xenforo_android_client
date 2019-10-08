package tech.sabtih.forumapp.models;

import java.util.ArrayList;

public class Forumcategory {
    private int id;
    private String title;
    private ArrayList<Forum> childs;


    public Forumcategory(int id, String title, ArrayList<Forum> childs) {
        this.id = id;
        this.title = title;
        this.childs = childs;
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

    public ArrayList<Forum> getChilds() {
        return childs;
    }

    public void setChilds(ArrayList<Forum> childs) {
        this.childs = childs;
    }
}

