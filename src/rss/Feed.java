package rss;

import utils.WebPage;
import java.util.ArrayList;

public class Feed
{
    private static final String search = "<title>";

    private String address;
    private String lastMessage;
    private String name;
    private int titleItr;

    public Feed(String name, String address) {
        this.address = address;
        this.name = name;
        lastMessage = "";
    }

    public String getName() { return name; }

    public String check() throws Exception {
        WebPage entry = WebPage.loadWebPage(address, "UTF-8");
        String content = entry.getContent();

        String feedName;
        String message;

        titleItr = 0;

        feedName = findNextTitle(content);
        message = findNextTitle(content);

        if(message == null || feedName == null || message.equals(lastMessage))
            return null;

        lastMessage = message;
        return name + ": " + message;
    }

    public ArrayList<String> getLastMessages(int count) throws Exception {
        WebPage entry = WebPage.loadWebPage(address, "UTF-8");
        String content = entry.getContent();

        titleItr = 0;
        findNextTitle(content); // First is feed name

        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i < count; ++i) {
            String msg = findNextTitle(content);
            if(msg == null)
                break;
            list.add(msg);
        }
        return list;
    }

    private String findNextTitle(String content) {
        int index = content.indexOf(search, titleItr);
        if(index == -1)
            return null;

        int begin = index + search.length();

        index = content.indexOf("</title>", begin);
        if(index == -1)
            return null;

        titleItr = index;
        return content.substring(begin, index);
    }
};