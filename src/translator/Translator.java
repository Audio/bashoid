package translator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import bashoid.Message;
import bashoid.Addon;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import utils.WebPage;

import static utils.Constants.*;


public class Translator extends Addon {

    private static final String LANGS[] =
    {
        "cz", "en", "ru", "de", "fr", "it", "es", "sk"
    };
    private static final byte LANGS_COUNT = (byte) LANGS.length;
    private static final String NO_TRANSLATION = "No translation found";
    private static final byte MAX_TRANSLATION_COUNT = 5;


    private String getAddress(String query, String langFrom, String langTo) throws UnsupportedEncodingException {
        query = URLEncoder.encode(query, "UTF-8");
        return "http://slovnik.seznam.cz/" + langFrom + "-" + langTo + "/?q=" + query;
    }

    private String loadPage(String address) throws Exception {
        WebPage entry = WebPage.loadWebPage(address, "UTF-8");
        return entry.getContent();
    }

    private String getTranslation(String address) throws Exception {
        String content = loadPage(address);

        if(content.indexOf("nebylo nic nalezeno") != NOT_FOUND || content.indexOf("li jste hledat?") != NOT_FOUND)
            return NO_TRANSLATION;

        String[] translations = getAllPossibleTranslations(content);
        String output = "";
        if (translations.length > 0) {

            for (byte i = 0; i < translations.length && i < MAX_TRANSLATION_COUNT; ++i)
                output += translations[i] + ", ";
            output = output.substring(0, output.length() - 2);

            if (translations.length > MAX_TRANSLATION_COUNT)
                output += " | " + address;

            return output;

        } else {
            return NO_TRANSLATION;
        }
    }

    private String[] getAllPossibleTranslations(String content) {
        Document doc = Jsoup.parse( content.replaceAll("<br />", "<span>#</span>") );
        Element fastMeanings = doc.getElementById("fastMeanings");
        if (fastMeanings != null) {
            String text = fastMeanings.text().replaceAll(" ,", ",");
            return text.substring(0, text.length() - 2).split(" # ");
        } else {
            Element moreResults = doc.getElementById("results");
            Elements links = moreResults.getElementsByTag("a");
            return links.text().split(" ");
        }
    }

    private String getLang(String message, boolean from) {
        return from ? message.substring(0, 2) : message.substring(6, 8);
    }

    public boolean isLangAllowed(String lang) {
        for(byte i = 0; i < LANGS_COUNT; ++i)
            if(lang.equals(LANGS[i]))
                return true;
        return false;
    }

    @Override
    public boolean shouldReact(Message message) {
        String msg = message.text;
        return (msg.length() > 8 && msg.indexOf("to") == 3 && msg.indexOf(' ') == 2
                && msg.indexOf(' ', 3) == 5);
    }

    @Override
    protected void setReaction(Message message) {
        try {
            String langFrom = getLang(message.text, true);
            String langTo = getLang(message.text, false);
            if (isLangAllowed(langFrom) && isLangAllowed(langTo)) {
                if (langFrom.equals(langTo)) {
                    reaction.add(NO_TRANSLATION);
                } else {
                    String response = getTranslation(getAddress(message.text.substring(9), langFrom, langTo));
                    reaction.add(response);
                }
            }
        } catch (Exception e) {
            setError(e);
        }
    }

}
