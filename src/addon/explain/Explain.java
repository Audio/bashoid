package addon.explain;

import bashoid.Addon;
import bashoid.Message;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import utils.Formatter;
import utils.WebPage;


public class Explain extends Addon {

    private static final String ENCODING = "UTF-8";


    private String getDefinition(String url) throws Exception {
        WebPage page = WebPage.loadWebPage(url, ENCODING);
        return getDefinitionFromHTML(page);
    }

    private String getDefinitionFromHTML(WebPage pg) {
        Element expl = Jsoup.parse( pg.getContent() ).getElementsByAttributeValue("class", "td3n2").first();
        return Formatter.removeHTML( expl.text() );
    }

    private String getUrl(String msg) throws UnsupportedEncodingException {
        msg = msg.substring(8);
        msg = URLEncoder.encode(msg, ENCODING);
        return "http://dictionary.reference.com/browse/" + msg;
    }

    @Override
    public boolean shouldReact(Message message) {
        return message.text.startsWith("explain ");
    }

    @Override
    protected void setReaction(Message message) {
        try {
            String url = getUrl(message.text);
            reaction.add( getDefinition(url) );
        } catch (Exception e) {
            setError("Cannot load given URL.", e);
        }
    }

}
