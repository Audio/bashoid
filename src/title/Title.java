package title;

import bashoid.Addon;
import java.text.ParseException;
import utils.*;

import static utils.Constants.*;


public class Title extends Addon {

    private String loadTitle(String url) throws Exception {
        WebPage page = WebPage.loadWebPage(url, "UTF-8");
        return getTitleFromRawHTML(page);
    }

    private String getTitleFromRawHTML(WebPage entry) throws ParseException {
        String content = entry.getContent();
        String title = XMLParser.getSnippet(content, "<title>", "</title>");
        return Formatter.removeHTML(title);
    }

    private String getUrl(String message) {
        message = message.substring(2);
        int pos;
        if ((pos = message.indexOf(" ") ) != NOT_FOUND)
            message = message.substring(0, pos);

        return message;
    }

    @Override
    public boolean shouldReact(String message) {
        return message.startsWith("t http");
    }

    @Override
    protected void setReaction(String message, String author) {
        try {
            String url = getUrl(message);
            reaction.add( loadTitle(url) );
        } catch (ParseException pe) {
            setError("Given page does NOT contain the title tag.", pe);
        } catch (Exception e) {
            setError("Cannot load given URL.", e);
        }
    }

}
