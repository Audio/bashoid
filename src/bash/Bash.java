package bash;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import utils.Cooldown;
import utils.WebPage;


public class Bash {

    private ArrayList<Quote> quotes;
    private ArrayList<String> output;

    private static Cooldown cooldown;
    private static HashMap<String, String> tagList;

    public Bash() {
        init();
        if ( isOnCooldown() )
            setErrorOutput();
        else
            downloadParseOutput();
    }

    private void init() {
        if (cooldown == null)
            cooldown = new Cooldown(30);

        tagList = new HashMap<String, String>();
        tagList.put("&lt;",   "<");
        tagList.put("&gt;",   ">");
        tagList.put("&quot;", "\"");
        tagList.put("&apos;", "'");
        tagList.put("&#039;", "'");
        tagList.put("&nbsp;", " ");
        tagList.put("&amp;",  "&");
        tagList.put("<br />",  "");
        tagList.put("<span style=\"color: ;\">", "");
        tagList.put("</span>", "");
    }

    private boolean isOnCooldown() {
        return cooldown.isActive();
    }

    private void downloadParseOutput() {
        try {
            WebPage page = loadWebPage();
            quotes = Parser.getQuotes(page, 50, 2500);
            sortQuotesByScore();
            takeOnlyTopTenQuotes();
            shuffleQuotes();
            takeOneQuoteAndSetOutput();
            cooldown.start();
        } catch (Exception e) {
            System.err.println("Cannot parse bash.org webpage");
            e.printStackTrace();
        }
    }

    public WebPage loadWebPage() throws Exception {
        return WebPage.loadWebPage("http://bash.org/?random1");
    }

    private void sortQuotesByScore() {
        Collections.sort(quotes);
    }

    private void takeOnlyTopTenQuotes() {
        quotes.subList(10, 50).clear();
    }

    private void shuffleQuotes() {
        Collections.shuffle(quotes);
    }

    private void takeOneQuoteAndSetOutput() {
        Quote quote = takeQuote(6);
        setOutput(quote);
    }

    private Quote takeQuote(int maxLinesLimit) {
        for ( Quote q : quotes ) {
            if ( q.lines() <= maxLinesLimit )
                return q;
        }

        return quotes.get(0);
    }

    private String removeHTML(String htmlToPlain) {
        for ( Map.Entry<String, String> entry : tagList.entrySet() )
            htmlToPlain = htmlToPlain.replace( entry.getKey(), entry.getValue() );

        return htmlToPlain;
    }

    private void setOutput(Quote quote) {
        String quoteContent = removeHTML( quote.getContent() );
        String url = "-- http://bash.org/?" + quote.getTextId() + " -- Next bash at " + getNextBashTime();

        output = new ArrayList<String>();
        String[] lines = quoteContent.split("\n");
        for ( String line : lines )
            output.add( line.trim() );

        output.add(url);
    }

    private void setErrorOutput() {
        String error = "I'm currently relaxing. I'll be back in " + cooldown.remainingSeconds() + " seconds.";
        output = new ArrayList<String>();
        output.add(error);
    }

    public ArrayList<String> getOutput() {
        return output;
    }

    public static boolean isBashMessage(String message) {
        return message.equals("bash");
    }

    private String getNextBashTime() {
        long nextCD = cooldown.nextCooldownFromNow() * 1000L;
        Date date = new Date(nextCD);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(date);
    }
}
