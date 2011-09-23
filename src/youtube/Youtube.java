package youtube;

import bashoid.Addon;
import java.util.ArrayList;
import utils.Formatter;
import utils.WebPage;
import utils.XMLParser;

import static utils.Constants.*;


public class Youtube extends Addon {

    private static final int VIDEO_ID_LENGTH = 11;
    private ArrayList<LinkInfo> cache = new ArrayList<LinkInfo>();


    private void downloadParseSave(String videoID) throws Exception {
        WebPage entry = loadVideoEntry(videoID);
        String title = getVideoTitleFromRawXML(entry);
        cache.add( new LinkInfo(videoID, title) );
    }

    private WebPage loadVideoEntry(String videoID) throws Exception {
        return WebPage.loadWebPage("http://gdata.youtube.com/feeds/api/videos/" + videoID, "UTF-8");
    }

    private String getVideoTitleFromRawXML(WebPage entry) throws Exception {
        String content = entry.getContent();
        String title = XMLParser.getSnippet(content, "<title type='text'>", "</title>");
        return Formatter.removeHTML(title);
    }

    private void downloadIfNeeded(String videoID) throws Exception {
        if ( !cacheContains(videoID) )
            downloadParseSave(videoID);
    }

    private String getVideoIDOrEmptyString(String message) {
        int beginPosition = getPositionWhereVideoIDStarts(message);
        if (beginPosition == NOT_FOUND)
            return "";

        return message.substring(beginPosition, beginPosition + VIDEO_ID_LENGTH);
    }

    private int getPositionWhereVideoIDStarts(String message) {
        int pos = videoIDPosition(message, "youtu.be/");
        if (pos != NOT_FOUND)
            return pos;

        pos = message.indexOf("youtube.com/watch");
        return (pos != NOT_FOUND) ? videoIDPosition(message, "v=", pos) : NOT_FOUND;
    }

    private int videoIDPosition(String message, final String URL_PATTERN) {
        return videoIDPosition(message, URL_PATTERN, 0);
    }

    private int videoIDPosition(String message, final String URL_PATTERN, int beginPosition) {
        int position = message.indexOf(URL_PATTERN, beginPosition);
        return (position != NOT_FOUND) ? position + URL_PATTERN.length() : NOT_FOUND;
    }

    private boolean cacheContains(String videoID) {
        return getCachedInfo(videoID) != null;
    }

    private LinkInfo getCachedInfo(String videoID) {
        for (LinkInfo li : cache)
            if ( li.hasVideoID(videoID) )
                return li;

        return null;
    }

    @Override
    public boolean shouldReact(String message) {
        return !getVideoIDOrEmptyString(message).equals("");
    }

    @Override
    protected void setReaction(String message, String author) {
        try {
            String newVideoID = getVideoIDOrEmptyString(message);
            downloadIfNeeded(newVideoID);
            LinkInfo li = getCachedInfo(newVideoID);
            reaction.add("YouTube: " + li.title() );
        } catch (Exception e) {
            setError(e);
        }
    }

}
