package rss;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import utils.WebPage;
import utils.XMLParser;
import utils.Formatter;

public class Feed
{
    protected static final String SEARCH = "<title>";

    protected String address;
    protected String lastMessage;
    protected String name;
    protected int titleItr;


    public Feed(String name, String address) {
        this.address = address;
        this.name = name;
        lastMessage = "";
    }

    public String getName() { return name; }

    public List<String> check(byte maxMsgsCount) throws IOException {
        WebPage entry = WebPage.loadWebPage(address, "UTF-8");
        String content = entry.getContent();

        String message;
        String firstMessage = null;
        List<String> newEntries = new ArrayList<String>();

        titleItr = 0;

        String feedName = findNextTitle(content);
        if (feedName == null)
            return newEntries;

        for (byte count = 1; count <= maxMsgsCount; ++count) {
            message = findNextTitle(content);

            if(message == null || message.equals(lastMessage))
                break;

            if (firstMessage == null)
                firstMessage = message;

            message = Formatter.removeHTML(message);

            newEntries.add(name + ": " + message);
        };

        if (firstMessage != null)
            lastMessage = firstMessage;

        return newEntries;
    }

    public ArrayList<String> getLastMessages(int count) throws IOException {
        WebPage entry = WebPage.loadWebPage(address, "UTF-8");
        String content = entry.getContent();

        titleItr = 0;
        findNextTitle(content); // First is feed name

        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i < count; ++i) {
            String msg = findNextTitle(content);
            if(msg == null)
                break;
            list.add(Formatter.removeHTML(msg));
        }
        return list;
    }

    protected String findNextTitle(String content) {
        try {
            String title = XMLParser.getSnippet(content, titleItr, SEARCH, "</title>");
            titleItr = XMLParser.getNextOccurrenceIndex();
            return title;
        } catch (ParseException e) {
            return null;
        }
    }
};