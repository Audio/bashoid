package title;

import bashoid.Addon;
import utils.Formatter;
import utils.WebPage;
import utils.XMLParser;


public class Title extends Addon {

    private static final String MESSAGE_PREFIX = "t http://";


    private String loadTitle(String url) throws Exception {
        WebPage page = WebPage.loadWebPage(url, "UTF-8");
        return getTitleFromRawHTML(page);
    }

    private String getTitleFromRawHTML(WebPage entry) throws Exception {
        String content = entry.getContent();
        String title = XMLParser.getSnippet(content, "<title>", "</title>");
        return Formatter.removeHTML(title);
    }

    private String getUrl(String message) {
        return message.substring(2);
    }

    @Override
    public boolean shouldReact(String message) {
        return message.startsWith(MESSAGE_PREFIX);
    }

    @Override
    protected void setReaction(String message, String author) {
        try {
            String url = getUrl(message);
            reaction.add( loadTitle(url) );
        } catch (Exception e) {
            setError(e);
        }
    }

}
