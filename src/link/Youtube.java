package link;

import utils.Formatter;
import utils.WebPage;


public class Youtube {

    private static final int VIDEO_ID_LENGTH = 11;
    private static final int NOT_FOUND = -1;

    public static void downloadParseSave(String videoID, String author) {
        try {
            WebPage entry = loadVideoEntry(videoID);
            String title = getVideoTitleFromRawXML(entry);
            YoutubeCache.add( new LinkInfo(videoID, author, title) );
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
        return Formatter.removeHTML( content.substring(begin, end) );
    }

    public static boolean setVideoIDIfPresent(String message, String author) {
        String newVideoID = getVideoIDOrEmptyString(message);
        boolean isVideoPresent = !newVideoID.equals("");
        if (isVideoPresent) {
            saveToCacheIfNeeded(newVideoID, author);
            YoutubeCache.setLastUsed(newVideoID, author);
        }
        return isVideoPresent;
    }

    private static void saveToCacheIfNeeded(String videoID, String autor) {
        if ( !YoutubeCache.contains(videoID) )
            downloadParseSave(videoID, autor);
    }

    private static String getVideoIDOrEmptyString(String message) {
        int beginPosition = getPositionWhereVideoIDStarts(message);
        if (beginPosition == NOT_FOUND)
            return "";

        return message.substring(beginPosition, beginPosition + VIDEO_ID_LENGTH);
    }

    private static int getPositionWhereVideoIDStarts(String message) {
        int pos = videoIDPosition(message, "youtube.com/watch?v=");
        return (pos == NOT_FOUND) ? videoIDPosition(message, "youtu.be/") : pos;
    }

    private static int videoIDPosition(String message, final String URL_PATTERN) {
        int position = message.indexOf(URL_PATTERN);
        return (position != NOT_FOUND) ? position + URL_PATTERN.length() : NOT_FOUND;
    }

    public static String getLastUsedLinkInfo(boolean shortVersion) {
        try {
            LinkInfo li = YoutubeCache.getLastInfo();
            if (shortVersion)
                return "YouTube: " + li.title();
            else
                return li.title() + " | http://youtu.be/" + li.videoID() + " | "
                                  + "shared by " + li.author()  + " at "
                                  + li.formattedTimeOfLastUsage();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
