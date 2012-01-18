package youtube;

import bashoid.Message;
import bashoid.Addon;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import utils.*;

import static utils.Constants.*;


public class Youtube extends Addon {

    private static final int VIDEO_ID_LENGTH = 11;
    private ArrayList<LinkInfo> cache = new ArrayList<LinkInfo>();


    private void downloadParseSave(String videoID) throws Exception {
        WebPage entry = loadVideoEntry(videoID);
        String title = getVideoTitleFromRawXML(entry);
        cache.add( new LinkInfo(videoID, title) );
    }

    private WebPage loadVideoEntry(String videoID) throws IOException {
        return WebPage.loadWebPage("http://gdata.youtube.com/feeds/api/videos/" + videoID, "UTF-8");
    }

    private String getVideoTitleFromRawXML(WebPage entry) throws ParseException {
        String title = Jsoup.parse( entry.getContent() ).title();
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
    public boolean shouldReact(Message message) {
        return !getVideoIDOrEmptyString(message.text).equals("");
    }

    @Override
    protected void setReaction(Message message) {
        try {
            String newVideoID = getVideoIDOrEmptyString(message.text);
            downloadIfNeeded(newVideoID);
            LinkInfo li = getCachedInfo(newVideoID);
            reaction.add("YouTube: " + li.title() );
        } catch (Exception e) {
            setError(e);
        }
    }

}
