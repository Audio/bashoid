package rss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import utils.WebPage;


public class Feed {

    protected String address;
    protected String name;
    protected String lastKnownTitle;
    protected List<String> lastFiveTitles;


    public Feed(String name, String address) {
        this.address = address;
        this.name = name;
        this.lastKnownTitle = "";
        this.lastFiveTitles = new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public List<String> check(byte maxMsgsCount) throws IOException {
        WebPage entry = WebPage.loadWebPage(address, "UTF-8");
        Elements titles = Jsoup.parse( entry.getContent() ).getElementsByTag("title");
        titles.remove(0);
        final int TITLES_COUNT = titles.size();

        String newestTitle = null;
        List<String> newTitles = new ArrayList<String>();

        for (byte i = 0; i < maxMsgsCount && i < TITLES_COUNT; ++i) {
            String title = titles.get(i).text();

            if( title.equals(lastKnownTitle) )
                break;
            else if (newestTitle == null)
                newestTitle = title;

            newTitles.add(name + ": " + title);
        };

        if (newestTitle != null) {
            lastKnownTitle = newestTitle;
            addToCache(newTitles);
        }

        return newTitles;
    }

    protected void addToCache(List<String> newTitles) {
        lastFiveTitles.addAll(newTitles);
        int endIndex = Math.min(5, lastFiveTitles.size() );
        lastFiveTitles = lastFiveTitles.subList(0, endIndex);
    }

    public List<String> getLastMessages(int count) {
        return lastFiveTitles;
    }

};