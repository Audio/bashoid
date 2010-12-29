package bash;

import java.util.ArrayList;
import utils.WebPage;


public class Parser {

    static ArrayList<Quote> getQuotes(WebPage page, final int COUNT, int start) {
        ArrayList<Quote> quotes = new ArrayList<Quote>();
        String content = page.getContent();

        for (int i = 0; i < COUNT; ++i) {
            String html = getQuoteHTML(start, content);
            int end = start + html.length();
            quotes.add( getQuote(html) );
            start = end;
        }

        return quotes;
    }

    private static String snippet(String content, int startPosition, String tagStart, String tagStop) {
        int start = content.indexOf(tagStart, startPosition);
        if (start == -1)
            return "";

        start += tagStart.length();
        int stop = content.indexOf(tagStop, start);
        return (stop == -1) ? content.substring(start) : content.substring(start, stop);
    }

    private static String getQuoteHTML(int startPosition, String content) {
        return snippet(content, startPosition, "class=\"quote\"", "class=\"quote\"");
    }

    private static Quote getQuote(String quoteHTML) {
        String quoteId    = snippet(quoteHTML, 100, "rox=", "\"");
        String quoteScore = snippet(quoteHTML, 120, "+</a>(", ")");
        String quoteText  = snippet(quoteHTML, 300, "class=\"qt\">", "</p>");

        int id =  Integer.parseInt(quoteId);
        int score =  Integer.parseInt(quoteScore);

        return new Quote(quoteText, score, id);
    }

}
