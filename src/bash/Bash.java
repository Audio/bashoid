package bash;

import bashoid.Addon;
import bashoid.Message;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import utils.*;


public class Bash extends Addon {

    private ArrayList<Quote> quotes;
    private Cooldown cooldown = new Cooldown(120);


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
        } catch (IOException ioe) {
            setError("Bash.org is unreachable.", ioe);
        } catch (ParseException pe) {
            setError("Error while parsing the source.", pe);
        }
    }

    private WebPage loadWebPage() throws IOException {
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

        String[] lines = quoteContent.split("\n");
        for ( String line : lines )
            reaction.add( line.trim() );

        reaction.add(url);
    }

    private void setErrorOutputBecauseOfCooldown() {
        String error = "I'm currently relaxing. I'll be back in "
                     + timeToString( cooldown.remainingSeconds() ) + ".";
        setError(error);
    }

    @Override
    protected void setReaction(Message message) {
        if ( isOnCooldown() )
            setErrorOutputBecauseOfCooldown();
        else
            downloadParseOutput();
    }

    @Override
    public boolean shouldReact(Message message) {
        return message.text.equals("bash");
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
