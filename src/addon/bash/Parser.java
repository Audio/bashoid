package addon.bash;

import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.WebPage;


public class Parser {

    static ArrayList<Quote> getQuotes(WebPage page) {
        ArrayList<Quote> quotes = new ArrayList<>();
        Element container = Jsoup.parse( page.getContent() ).getElementsByAttribute("valign").first();
        Elements headers = container.getElementsByClass("quote");
        Elements bodies = container.getElementsByClass("qt");

        final int COUNT = headers.size();
        for (int i = 0; i < COUNT; ++i) {
            String[] body = bodies.get(i).html().split("<br />");
            Element header = headers.get(i);

            String quoteId = header.getElementsByTag("b").first().text().substring(1);
            int id = Integer.parseInt(quoteId);

            String quoteScore = header.ownText().substring(1, header.ownText().length() - 1);
            int score = Integer.parseInt(quoteScore);

            quotes.add( new Quote(body, score, id) );
        }

        return quotes;
    }

}
