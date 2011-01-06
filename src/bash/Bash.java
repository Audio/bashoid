package bash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import utils.Cooldown;
import utils.WebPage;


public class Bash {

    private ArrayList<Quote> quotes;
    private ArrayList<String> output;

    private static Cooldown cooldown;
    private static HashMap<String, String> tagList;

    static {
        cooldown = new Cooldown(120);

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

    public Bash() {
        if ( isOnCooldown() )
            setErrorOutputBecauseOfCooldown();
        else
            downloadParseOutput();
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
            setErrorOutput("Bash.org is currently down.");
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
        String url = "-- http://bash.org/?" + quote.getTextId() + " -- Next bash in " + getNextBashTime();

        output = new ArrayList<String>();
        String[] lines = quoteContent.split("\n");
        for ( String line : lines )
            output.add( line.trim() );

        output.add(url);
    }

    private void setErrorOutputBecauseOfCooldown() {
        String error = "I'm currently relaxing. I'll be back in " + cooldown.remainingSeconds() + " seconds.";
        setErrorOutput(error);
    }

    private void setErrorOutput(String reason) {
        output = new ArrayList<String>();
        output.add(reason);
    }

    public ArrayList<String> getOutput() {
        return output;
    }

    public static boolean isBashMessage(String message) {
        return message.equals("bash");
    }

    private String getNextBashTime() {
        long seconds = cooldown.length();
        long minutes = seconds / 60;
        seconds = seconds - minutes*60;
        return timeToString(minutes, seconds);
    }

    private String timeToString(long minutes, long seconds) {
        if (minutes == 0 && seconds == 0)
            return "0 seconds";
        else if (minutes == 0)
            return seconds + " seconds";
        else if (seconds == 0)
            return minutes + " minutes";
        else
            return minutes + " minutes and " + seconds + " seconds";
    }

}
