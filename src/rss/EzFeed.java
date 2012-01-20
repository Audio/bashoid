package rss;


public class EzFeed extends Feed {

    public EzFeed() {
        super("EZTV", "http://www.ezrss.it/feed/");
        displayLinks = true;

        // move to config?
        titlePatterns.add("Castle");
        titlePatterns.add("Breaking Bad");
        titlePatterns.add("Futurama");
        titlePatterns.add("Simpsons");
        titlePatterns.add("Game of Thrones");
        titlePatterns.add("The Big Bang Theory"); 
        titlePatterns.add("How I Met Your Mother");
        titlePatterns.add("House");
    }

};