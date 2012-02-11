package addon.rss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.WebPage;

import static utils.Constants.*;


public class Feed {

    protected String address;
    protected String name;
    protected String lastKnownTitle;
    protected List<String> lastFiveTitles;
    protected List<String> titlePatterns;
    protected boolean displayLinks;
    protected enum Type { Atom, RSS, Undefined }
    protected Type type;


    public Feed(String name, String address) {
        this.address = address;
        this.name = name;
        this.lastKnownTitle = "";
        this.lastFiveTitles = new ArrayList<String>();
        this.titlePatterns = new ArrayList<String>();
        this.displayLinks = false;
        this.type = Type.Undefined;
    }

    public String getName() {
        return name;
    }

    public List<String> check(byte maxMsgsCount) throws IOException {
        WebPage entry = WebPage.loadWebPage(address, "UTF-8");
        String content = replaceLinks( entry.getContent() );
        defineFeedTypeIfNecessary(content);
        Elements items = Jsoup.parse(content).getElementsByTag( getItemTag() );
        int itemsCount = items.size();

        String newestTitle = null;
        List<String> newTitles = new ArrayList<String>();

        for (byte i = 0; i < maxMsgsCount && i < itemsCount; ++i) {
            String title = removeCDATA( items.get(i).getElementsByTag("title").text() );
            String link = getLink( items.get(i) );

            if( title.equals(lastKnownTitle) )
                break;

            if ( titleMatched(title) ) {
                String output = title;
                if (displayLinks)
                    output += " | " + link;
                newTitles.add(output);

                if (newestTitle == null)
                    newestTitle = title;
            }
        }

        if (newestTitle != null) {
            lastKnownTitle = newestTitle;
            addToCache(newTitles);
        }

        return newTitles;
    }

    protected void addToCache(List<String> newTitles) {
        lastFiveTitles.addAll(0, newTitles);
        int endIndex = Math.min(5, lastFiveTitles.size() );
        lastFiveTitles = lastFiveTitles.subList(0, endIndex);
    }

    protected void defineFeedTypeIfNecessary(String content)  {
        if (type == Type.Undefined) {
            int maxSearchLen = Math.min(300, content.length() );
            boolean isRSS = content.substring(0, maxSearchLen).indexOf("<rss") != NOT_FOUND;
            type = isRSS ? Type.RSS : Type.Atom;
        }
    }

    protected String getItemTag() {
        switch (type) {
            case Atom: return "entry";
            default: return "item";
        }
    }

    protected String getLink(Element item) {
        switch (type) {
            case Atom: return item.getElementsByTag("link").attr("href");
            default: return item.getElementsByTag("span").text();
        }
    }

    protected String replaceLinks(String content) {
        return content.replaceAll("<link>", "<span>").replaceAll("</link>", "</span>");
    }

    public List<String> getLastMessages(int count) {
        return lastFiveTitles;
    }

    protected boolean titleMatched(String title) {
        if ( titlePatterns.isEmpty() )
            return true;

        for(String pattern : titlePatterns)
            if( title.indexOf(pattern) != NOT_FOUND )
                return true;

        return false;
    }

    protected String removeCDATA(String title) {
        return ( title.indexOf("<![CDATA[") != NOT_FOUND )
               ? title.substring(9, title.length() - 3) : title;
    }

};