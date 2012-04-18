package addon.youtube;

import bashoid.Message;
import bashoid.Addon;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import utils.*;


public class Youtube extends Addon {

    private static final Pattern LONG_URL = Pattern.compile("youtube\\.com.*v=([^&$]{11})(&|#| |$)");
    private static final Pattern SHORT_URL = Pattern.compile("youtu\\.be/([^&\\?$]{11})(&|#| |$)");
    private ArrayList<LinkInfo> cache = new ArrayList<LinkInfo>();

    private void downloadParseSave(String videoID) throws Exception {
        WebPage entry = loadVideoEntry(videoID);
        String title = getVideoTitleFromRawXML(entry);
        cache.add( new LinkInfo(videoID, title) );
    }

    private WebPage loadVideoEntry(String videoID) throws IOException {
        return WebPage.loadWebPage("http://gdata.youtube.com/feeds/api/videos/" + videoID, "UTF-8");
    }

    private String getVideoTitleFromRawXML(WebPage entry) {
        String title = Jsoup.parse( entry.getContent() ).title();
        return Formatter.removeHTML(title);
    }

    private void downloadIfNeeded(String videoID) throws Exception {
        if ( !cacheContains(videoID) )
            downloadParseSave(videoID);
    }

    private String getVideoID(String message) {
        Matcher matcher = LONG_URL.matcher(message);
        if ( !matcher.find() ) {
            matcher = SHORT_URL.matcher(message);
            matcher.find();
        }

        return matcher.group(1);
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
        return LONG_URL.matcher(message.text).find()
            || SHORT_URL.matcher(message.text).find();
    }

    @Override
    protected void setReaction(Message message) {
        try {
            String newVideoID = getVideoID(message.text);
            downloadIfNeeded(newVideoID);
            LinkInfo li = getCachedInfo(newVideoID);
            reaction.add("YouTube: " + li.title() );
        } catch (Exception e) {
            setError(e);
        }
    }

}
