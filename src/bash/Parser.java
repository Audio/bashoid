package bash;

import java.text.ParseException;
import java.util.ArrayList;
import utils.WebPage;
import utils.XMLParser;


public class Parser {

    static ArrayList<Quote> getQuotes(WebPage page, final int COUNT, int start) throws ParseException {
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

    private static String getQuoteHTML(int startPosition, String content) throws ParseException {
        return XMLParser.getSnippet(content, startPosition, "class=\"quote\"", "class=\"quote\"");
    }

    private static Quote getQuote(String quoteHTML) throws ParseException {
        String quoteId    = XMLParser.getSnippet(quoteHTML, 100, "rox=", "\"");
        String quoteScore = XMLParser.getSnippet(quoteHTML, 120, "+</a>(", ")");
        String quoteText  = XMLParser.getSnippet(quoteHTML, 300, "class=\"qt\">", "</p>");

        int id = Integer.parseInt(quoteId);
        int score = Integer.parseInt(quoteScore);

        return new Quote(quoteText, score, id);
    }

}
