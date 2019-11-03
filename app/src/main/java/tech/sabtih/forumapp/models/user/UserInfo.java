package tech.sabtih.forumapp.models.user;


import java.util.LinkedHashMap;
import java.util.Map;

public class UserInfo {
LinkedHashMap<String,String> aboutpairs;
    LinkedHashMap<String,String > interactpairs;
    private String about;


    public UserInfo(String about) {
        this.about = about;
    }

    public LinkedHashMap<String, String> getAboutpairs() {
        return aboutpairs;
    }

    public void setAboutpairs(LinkedHashMap<String, String> aboutpairs) {
        this.aboutpairs = aboutpairs;
    }

    public LinkedHashMap<String, String> getInteractpairs() {
        return interactpairs;
    }

    public void setInteractpairs(LinkedHashMap<String, String> interactpairs) {
        this.interactpairs = interactpairs;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }


}
