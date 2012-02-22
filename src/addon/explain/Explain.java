package addon.explain;

import bashoid.Addon;
import bashoid.Message;
import java.text.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.Formatter;
import utils.WebPage;


public class Explain extends Addon {
    
    private String getDefinition(String url) throws Exception {
        WebPage page = WebPage.loadWebPage(url, "UTF-8");
        return getDefinitionFromHTML(page);
    }
    
    private String getDefinitionFromHTML(WebPage pg) throws ParseException {
        Element expl = Jsoup.parse( pg.getContent() ).getElementsByAttributeValue("class", "td3n2").first();
        return Formatter.removeHTML( expl.text() );
    }
    
    private String GetUrl(String msg) {
        msg = msg.substring(8);
        msg.replaceAll(" ", "%20");
        return "http://dictionary.reference.com/browse/" + msg;
    }
    
    @Override
    public boolean shouldReact(Message message) {
        return message.text.startsWith("explain");
    }

    @Override
    protected void setReaction(Message message) {
        try {
            String url = GetUrl(message.text);
            reaction.add( getDefinition(url) );
        } catch (ParseException pe) {
            setError("No meaning found, expression is probably nonsens!", pe);
        } catch (Exception e) {
            setError("Cannot load given URL.", e);
        }
    }

}
