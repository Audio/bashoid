package link;

import utils.WebPage;


public class Youtube {

    private static String videoID = "";
    private static final int VIDEO_ID_LENGTH = 11;
    private static final int NOT_FOUND = -1;

    private String output;

    public Youtube() {
        downloadParseOutput();
    }

    private void downloadParseOutput() {
        try {
            WebPage entry = loadVideoEntry();
            String title = getVideoTitle(entry);
            setOutput(title);
        } catch (Exception e) {
            setOutput("Unknown video");
        }
    }

    private WebPage loadVideoEntry() throws Exception {
        if ( videoID.length() == 0 )
            throw new Exception("No youtube URL has been received.");

        return WebPage.loadWebPage("http://gdata.youtube.com/feeds/api/videos/" + videoID);
    }

    private String getVideoTitle(WebPage entry) {
        String content = entry.getContent();
        String toSearch = "<title type='text'>";
        int pos = content.indexOf(toSearch);
        if (pos == NOT_FOUND)
            return "That link was seriously damaged.";

        int begin = pos + toSearch.length();
        int end = content.indexOf("</title>", begin);
        return content.substring(begin, end);
    }

    public String getOutput() {
        return output;
    }

    private void setOutput(String output) {
        this.output = output;
    }

    public static void setVideoIDIfPresent(String message) {
        String newVideoID = getVideoIDOrEmptyString(message);
        if ( newVideoID.length() > 0 )
            videoID = newVideoID;
    }

    private static String getVideoIDOrEmptyString(String message) {
        String toSearch = "youtube.com/watch?v=";
        int pos = message.indexOf(toSearch);
        if (pos == NOT_FOUND)
            return "";

        int begin = pos + toSearch.length();
        return message.substring(begin, begin + VIDEO_ID_LENGTH);
    }

    public static boolean isYoutubeMessage(String message) {
        return message.equals("y");
    }

}
