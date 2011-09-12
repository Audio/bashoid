package translator;

import bashoid.Addon;
import utils.WebPage;

import static utils.Constants.*;


public class Translator extends Addon {

    private static final String langs[] =
    {
        "cz", "en", "ru", "de", "fr", "it", "es", "sk"
    };
    private static final byte langsCount = 8;


    private String loadTranslation(String query, String langFrom, String langTo) throws Exception {
        String address = "http://slovnik.seznam.cz/" + langFrom + "-" + langTo + "/?q=" + query;
        WebPage entry = WebPage.loadWebPage(address, "UTF-8");
        return getReponseFromRawHTML(entry);
    }

    private String getReponseFromRawHTML(WebPage entry) throws Exception {
        String content = entry.getContent();

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

        return result;
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
    public boolean shouldReact(String message) {
         return (message.length() > 8 && message.indexOf("to") == 3 && message.indexOf(' ') == 2
                && message.indexOf(' ', 3) == 5);
    }

    @Override
    protected void setReaction(String message, String author) {
        try {
            String langFrom = getLang(message, true);
            String langTo = getLang(message, false);
            if(isLangAllowed(langFrom) && isLangAllowed(langTo))
            {
                String response = loadTranslation(message.substring(9), langFrom, langTo);
                reaction.add(response);
            }
        } catch (Exception e) {
            setError(e);
        }
    }

}
