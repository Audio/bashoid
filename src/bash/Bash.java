package bash;

import java.util.ArrayList;
import java.util.Collections;
import utils.Cooldown;
import utils.Formatter;
import utils.WebPage;


public class Bash {

    private ArrayList<Quote> quotes;
    private ArrayList<String> output;
    private boolean errorOccured;

    private static Cooldown cooldown;

    static {
        cooldown = new Cooldown(120);
    }

    public Bash() {
        if ( isOnCooldown() )
            setErrorOutputBecauseOfCooldown();
        else
            downloadParseOutput();
    }

    public boolean errorOccured() {
        return errorOccured;
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

    private WebPage loadWebPage() throws Exception {
        return WebPage.loadWebPage("http://bash.org/?random1", "ISO-8859-1");
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
        return Formatter.removeHTML(htmlToPlain);
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
        String error = "I'm currently relaxing. I'll be back in "
                     + timeToString( cooldown.remainingSeconds() ) + ".";
        setErrorOutput(error);
    }

    private void setErrorOutput(String reason) {
        output = new ArrayList<String>();
        output.add(reason);
        errorOccured = true;
    }

    public ArrayList<String> getOutput() {
        return output;
    }

    public static boolean isBashMessage(String message) {
        return message.equals("bash");
    }

    private String getNextBashTime() {
        return timeToString( cooldown.length() );
    }

    private String timeToString(long seconds) {
        long minutes = seconds / 60;
        seconds = seconds - minutes*60;

        if (minutes == 0 && seconds == 0)
            return "zero seconds";
        else if (minutes == 0)
            return secondsToString(seconds);
        else if (seconds == 0)
            return minutesToString(minutes);
        else
            return minutesToString(minutes) + " and " + secondsToString(seconds);
    }

    private String minutesToString(long minutes) {
        return minutes + " " + getWordInSingularOrPluralForm("minute", minutes);
    }

    private String secondsToString(long seconds) {
        return seconds + " " + getWordInSingularOrPluralForm("second", seconds);
    }

    private String getWordInSingularOrPluralForm(String word, long count) {
        return (count > 1) ? word + "s" : word;
    }

}
