package rss;

import java.io.IOException;
import java.util.*;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import utils.WebPage;

import static utils.Constants.*;


public class EzFeed extends Feed {

    private ArrayList<String> series;


    public EzFeed() {
        super("EZTV", "http://www.ezrss.it/feed/");

        series = new ArrayList<String>();

        // move to config?
        series.add("Castle");
        series.add("Breaking Bad");
        series.add("Futurama");
        series.add("Simpsons");
        series.add("Game of Thrones");
        series.add("The Big Bang Theory"); 
        series.add("How I Met Your Mother");
        series.add("House");
    }

    @Override
    public List<String> check(byte maxMsgsCount) throws IOException {
        WebPage entry = WebPage.loadWebPage(address, "UTF-8");
        String content = replaceLinks( entry.getContent() );
        Elements items = Jsoup.parse(content).getElementsByTag("item");
        final int ITEMS_COUNT = items.size();

        String newestTitle = null;
        List<String> newTitles = new ArrayList<String>();

        for (byte i = 0; i < maxMsgsCount && i < ITEMS_COUNT; ++i) {
            String title = removeCDATA( items.get(i).getElementsByTag("title").text() );
            String link = items.get(i).getElementsByTag("span").text();

            if( title.equals(lastKnownTitle) )
                break;

            if ( isShowTracked(title) ) {
                newTitles.add(name + ": " + title + " | " + link);

                if (newestTitle == null)
                    newestTitle = title;
            }
        };

        if (newestTitle != null) {
            lastKnownTitle = newestTitle;
            addToCache(newTitles);
        }

        return newTitles;
    }

    private boolean isShowTracked(String title) {
        for(String s : series)
            if(title.indexOf(s) != NOT_FOUND)
                return true;
        return false;
    }

    private String replaceLinks(String content) {
        return content.replaceAll("<link>", "<span>").replaceAll("</link>", "</span>");
    }

    private String removeCDATA(String title) {
        return title.substring(9, title.length() - 3);
    }

};