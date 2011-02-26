package link;

import java.util.Calendar;


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
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        return c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
    }

    public void setAuthorAndResetTime(String newAuthor) {
        author = newAuthor;
        timestamp = System.currentTimeMillis();
    }

    public boolean isSameAs(String anotherID) {
        return videoID.equals(anotherID);
    }

}
