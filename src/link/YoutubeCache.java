package link;

import java.util.ArrayList;


public class YoutubeCache {

    private static ArrayList<LinkInfo> links;

    static {
        links = new ArrayList<LinkInfo>();
    }

    public static void add(LinkInfo info) {
        links.add(info);
    }

    public static boolean contains(String videoID) {
        return getInfo(videoID) != null;
    }

    public static LinkInfo getInfo(String videoID) {
        for (LinkInfo li : links)
            if ( li.hasVideoID(videoID) )
                return li;

        return null;
    }

}
