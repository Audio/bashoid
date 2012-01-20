package addon.youtube;


public final class LinkInfo {

    private String videoID;
    private String title;

    public LinkInfo(String videoID, String title) {
        this.videoID = videoID;
        this.title = title;
    }

    public String videoID() {
        return videoID;
    }

    public String title() {
        return title;
    }

    public boolean hasVideoID(String videoID) {
        return this.videoID.equals(videoID);
    }

}
