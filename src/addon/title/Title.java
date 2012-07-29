package addon.title;

import bashoid.Addon;
import bashoid.Message;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import utils.*;


public class Title extends Addon {

    private static final Pattern PATTERN = Pattern.compile("^t +(http\\S+)");


    private String loadTitle(String url) throws Exception {
        WebPage page = WebPage.loadWebPage(url, "UTF-8");
        return getTitleFromRawHTML(page);
    }

    private String getTitleFromRawHTML(WebPage entry) {
        String title = Jsoup.parse( entry.getContent() ).title();
        return Formatter.removeHTML(title);
    }

    @Override
    public boolean shouldReact(Message message) {
        return PATTERN.matcher(message.text).find();
    }

    @Override
    protected void setReaction(Message message) {
        try {
            Matcher matcher = PATTERN.matcher(message.text);
            matcher.find();
            reaction.add( loadTitle( matcher.group(1) ) );
        } catch (Exception e) {
            setError("Cannot load given URL.", e);
        }
    }

}
