package tech.sabtih.forumapp.models;

public class Forum {
    private int id;
    private String title;
    private int discussions;
    private int messages;
    private String url;
    private String latest;
    private boolean unread;
    private String latestdate;
    private String type;


    public Forum(int id, String title, int discussions, int messages, String url, String latest, boolean unread, String latestdate, String type) {
        this.id = id;
        this.title = title;
        this.discussions = discussions;
        this.messages = messages;
        this.url = url;
        this.latest = latest;
        this.unread = unread;
        this.latestdate = latestdate;
        this.type = type;
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

    public int getDiscussions() {
        return discussions;
    }

    public void setDiscussions(int discussions) {
        this.discussions = discussions;
    }

    public int getMessages() {
        return messages;
    }

    public void setMessages(int messages) {
        this.messages = messages;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLatest() {
        return latest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public String getLatestdate() {
        return latestdate;
    }

    public void setLatestdate(String latestdate) {
        this.latestdate = latestdate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
