package addon.title;

import bashoid.Message;
import bashoid.Addon;
import java.text.ParseException;
import org.jsoup.Jsoup;
import utils.*;

import static utils.Constants.*;


public class Title extends Addon {

    private String loadTitle(String url) throws Exception {
        WebPage page = WebPage.loadWebPage(url, "UTF-8");
        return getTitleFromRawHTML(page);
    }

    private String getTitleFromRawHTML(WebPage entry) throws ParseException {
        String title = Jsoup.parse( entry.getContent() ).title();
        return Formatter.removeHTML(title);
    }

    private String getUrl(String message) {
        message = message.substring(2);
        int pos = message.indexOf(" ");
        if (pos != NOT_FOUND)
            message = message.substring(0, pos);

        return message;
    }

    @Override
    public boolean shouldReact(Message message) {
        return message.text.startsWith("t http");
    }

    @Override
    protected void setReaction(Message message) {
        try {
            String url = getUrl(message.text);
            reaction.add( loadTitle(url) );
        } catch (ParseException pe) {
            setError("Given page does NOT contain the title tag.", pe);
        } catch (Exception e) {
            setError("Cannot load given URL.", e);
        }
    }

}
