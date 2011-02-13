package link;


public class LinkInfo {

    private String videoID;
    private String author;
    private String title;
    private long   timestamp;

    public LinkInfo(String videoID, String author, String title) {
        this.videoID = videoID;
        this.author = author;
        this.title = title;
        setTimeOfLastUsageToNow();
    }

    public String videoID() {
        return videoID;
    }

    public String author() {
        return author;
    }

    public String title() {
        return title;
    }

    public long timeOfLastUsage() {
        return timestamp;
    }

    public void setTimeOfLastUsageToNow() {
        timestamp = System.currentTimeMillis() / 1000L;
    }

    public boolean isSameAs(String anotherID) {
        return videoID.equals(anotherID);
    }

}
