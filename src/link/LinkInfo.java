package link;

import utils.CurrentTime;


public final class LinkInfo {

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
        long diff = CurrentTime.inSeconds() - timestamp;
        return diff + " seconds";
    }

    public void setAuthorAndResetTime(String newAuthor) {
        author = newAuthor;
        timestamp = CurrentTime.inSeconds();
    }

    public boolean isSameAs(String anotherID) {
        return videoID.equals(anotherID);
    }

}
