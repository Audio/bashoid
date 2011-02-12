package link;

import utils.WebPage;


public class Youtube {

    private static final int VIDEO_ID_LENGTH = 11;
    private static final int NOT_FOUND = -1;

    public static void downloadParseTitle(String videoID) {
        try {
            WebPage entry = loadVideoEntry(videoID);
            String title = getVideoTitleFromRawXML(entry);
            saveTitle(videoID, title);
        } catch (Exception e) {
            System.err.println( e.getMessage() );
        }
    }

    private static WebPage loadVideoEntry(String videoID) throws Exception {
        return WebPage.loadWebPage("http://gdata.youtube.com/feeds/api/videos/" + videoID, "UTF-8");
    }

    private static String getVideoTitleFromRawXML(WebPage entry) throws Exception {
        String content = entry.getContent();
        String toSearch = "<title type='text'>";
        int pos = content.indexOf(toSearch);
        if (pos == NOT_FOUND)
            throw new Exception("Cannot find video title in the XML source.");

        int begin = pos + toSearch.length();
        int end = content.indexOf("</title>", begin);
        return content.substring(begin, end);
    }

    private static void saveTitle(String videoID, String title) {
        YoutubeCache.add(videoID, title);
    }

    public static void setVideoIDIfPresent(String message) {
        String newVideoID = getVideoIDOrEmptyString(message);
        if ( newVideoID.length() > 0 ) {
            downloadParseTitle(newVideoID);
            YoutubeCache.setLastUsed(newVideoID);
        }
    }

    private static String getVideoIDOrEmptyString(String message) {
        int beginPosition = getPositionWhenVideoIDStarts(message);
        if (beginPosition == NOT_FOUND)
            return "";

        return message.substring(beginPosition, beginPosition + VIDEO_ID_LENGTH);
    }

    private static int getPositionWhenVideoIDStarts(String message) {
        int standardPosition = videoIDPositionInStandardURL(message);
        return (standardPosition == NOT_FOUND) ? videoIDPositionInShortenURL(message) : standardPosition;
    }

    private static int videoIDPositionInStandardURL(String message) {
        int position = message.indexOf("youtube.com/watch?v=");
        return (position != NOT_FOUND) ? position + 20 : NOT_FOUND;
    }

    private static int videoIDPositionInShortenURL(String message) {
        int position = message.indexOf("youtu.be/");
        return (position != NOT_FOUND) ? position + 9 : NOT_FOUND;
    }

    public static boolean isYoutubeMessage(String message) {
        return message.equals("y");
    }

    public static String getLastUsedTitle() {
        return YoutubeCache.getLastTitle();
    }

}
