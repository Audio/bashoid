package rss;

import utils.WebPage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static utils.Constants.*;

public class EzFeed extends Feed
{
    private ArrayList<String> releases;
    private ArrayList<String> series;
    private Date lastDate;
    private DateFormat dateFormatter;

    public EzFeed() {
        super("EZTV", "http://rss.thepiratebay.org/user/d17c6a45441ce0bc0c057f19057f95e1");

        releases = new ArrayList<String>();
        series = new ArrayList<String>();
        lastDate = new Date(0);
        dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZZ", Locale.ENGLISH);

        // move to config?
        series.add("Castle");
        series.add("Breaking Bad");
        series.add("Futurama");
        series.add("Simpsons");
        series.add("Game of Thrones");
        series.add("The Big Bang Theory"); 
        series.add("How I Met Your Mother");
    }

    @Override
    public List<String> check(byte maxMsgsCount) throws Exception {
        WebPage entry = WebPage.loadWebPage(address, "UTF-8");
        String content = entry.getContent();

        String message;
        String link;
        List<String> newEntries = new ArrayList<String>();

        titleItr = 0;

        String feedName = findNextTitle(content);
        if (feedName == null)
            return newEntries;

        while(true) {
            message = findNextTitle(content);

            if(message == null || isOld(content))
                break;

            if(!isShowTracked(message))
                continue;
            
            link = findNextTag(content, "<link>", "</link>");
            
            if(link == null)
                continue;

            newEntries.add(message + " | " + link);
            releases.add(message + " | " + link);
        }

        lastDate = new Date();

        return newEntries;
    }

    @Override
    public ArrayList<String> getLastMessages(int count) throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        int size = releases.size();
        if(size == 0)
            return list;
        
        if(count > size)
            count = size;
        
        for(byte i = 0; i < count; ++i)
            list.add(releases.get(i));
        for(byte i = (byte)count; i < size; ++i)
            releases.remove(i);
        
        return list;
    }

    private String findNextTag(String content, String tag, String endTag) {
        int index = content.indexOf(tag, titleItr);
        if(index == NOT_FOUND)
            return null;

        int begin = index + tag.length();

        index = content.indexOf(endTag, begin);
        if(index == NOT_FOUND)
            return null;

        return content.substring(begin, index);
    }

    private boolean isShowTracked(String title) {
        for(String s : series)
            if(title.indexOf(s) != NOT_FOUND)
                return true;
        return false;
    }

    private boolean isOld(String content) {
        String time = findNextTag(content, "<pubDate>", "</pubDate>");
        if(time == null)
            return false;
        
        Date date = null;
        try{
            date = dateFormatter.parse(time);
        }
        catch(ParseException e){
        }
    
        if(date == null || date.before(lastDate))
            return true;
        return false;
    }
};