package link;


public class LinkInfo {

    private String videoID;
    private String author;
    private String title;
    private long   timestamp;

    public LinkInfo(String videoID, String author, String title) {
        this.videoID = videoID;
        this.title = title;
        setAuthorAndResetTime(author);
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

    public String formattedTimeOfLastUsage() {
        long diff = now() - timestamp;
        return diff + " seconds";
    }

    public void setAuthorAndResetTime(String newAuthor) {
        author = newAuthor;
        timestamp = now();
    }

    private long now() {
        return System.currentTimeMillis() / 1000L;
    }

    public boolean isSameAs(String anotherID) {
        return videoID.equals(anotherID);
    }

}
