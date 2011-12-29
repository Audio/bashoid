package translator;

import bashoid.Message;
import bashoid.Addon;
import utils.WebPage;

import static utils.Constants.*;


public class Translator extends Addon {

    private static final String langs[] =
    {
        "cz", "en", "ru", "de", "fr", "it", "es", "sk"
    };
    private static final byte langsCount = 8;


    private String getAddress(String query, String langFrom, String langTo) {
        return "http://slovnik.seznam.cz/" + langFrom + "-" + langTo + "/?q=" + query;
    }

    private String loadPage(String address) throws Exception {
        WebPage entry = WebPage.loadWebPage(address, "UTF-8");
        return entry.getContent();
    }

    private String getTranslation(String address) throws Exception {
        String content = loadPage(address);

        if(content.indexOf("nebylo nic nalezeno") != NOT_FOUND || content.indexOf("li jste hledat?") != NOT_FOUND)
            return "No translation found";

        String toSearch = "<div id=\"fastMeanings\">";
        int pos = content.indexOf(toSearch);
        if (pos == NOT_FOUND)
            throw new Exception("Cannot... nothing!");


        int begin = pos + toSearch.length();
        int end = content.indexOf("<br", begin);
        if (end == NOT_FOUND)
            end = content.indexOf("</div>", begin);

        String sub = content.substring(begin, end);
        String result = "";

        if(sub.indexOf("<span class=\"w\">") != NOT_FOUND && sub.indexOf("<a href") == NOT_FOUND)
            return "No translation found, it is probably the same in target language";

        toSearch = "\">";
        pos = sub.indexOf("<a href");
        int comma = 0;
        while(pos != NOT_FOUND)
        {
            pos = sub.indexOf(toSearch, pos);

            begin = pos + toSearch.length();
            end = sub.indexOf("</a>", begin);
            if (end == NOT_FOUND)
            {
                result = sub;
                break;
            }
            result += sub.substring(begin, end);
            pos = sub.indexOf("<a href", end);
            if(comma != NOT_FOUND)
            {
                comma = sub.indexOf("<span class=\"comma\">", end);
                if(comma != NOT_FOUND && comma < pos)
                    result += ",";
            }
            result += " ";
        }

        return result + "| " + address;
    }

    private String getLang(String message, boolean from) {
        return from ? message.substring(0, 2) : message.substring(6, 8);
    }

    public boolean isLangAllowed(String lang)
    {
        for(byte i = 0; i < langsCount; ++i)
            if(lang.equals(langs[i]))
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
            if(isLangAllowed(langFrom) && isLangAllowed(langTo))
            {
                String response = getTranslation(getAddress(message.text.substring(9), langFrom, langTo));
                reaction.add(response);
            }
        } catch (Exception e) {
            setError(e);
        }
    }

}
