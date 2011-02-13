package link;

import java.util.ArrayList;


public class YoutubeCache {

    private static ArrayList<LinkInfo> links;
    private static LinkInfo lastUsedVideoID;

    static {
        links = new ArrayList<LinkInfo>();
    }

    public static void add(LinkInfo info) {
        links.add(info);
    }

    public static boolean contains(String videoID) {
        for (LinkInfo li : links) {
            if ( li.isSameAs(videoID) )
                return true;
        }

        return false;
    }

    public static void setLastUsed(String videoID) {
        for (LinkInfo li : links)
            if ( li.isSameAs(videoID) )
                lastUsedVideoID = li;
    }

    public static LinkInfo getLastInfo() throws Exception {
        if (lastUsedVideoID == null)
            throw new Exception("No YouTube link has been shared yet");

        return lastUsedVideoID;
    }

}
