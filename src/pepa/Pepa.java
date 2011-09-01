package pepa;

import bashoid.Addon;
import java.net.URLEncoder;
import utils.WebPage;

import static utils.Constants.*;


public class Pepa extends Addon {

    private String loadResponse(String query) throws Exception {
        String postData = URLEncoder.encode("strText", "windows-1250") + "=" + URLEncoder.encode(query, "windows-1250");
        WebPage entry = WebPage.loadWebPage("http://pepa.vyskup.com/index.php", "windows-1250", postData);
        return getReponseFromRawHTML(entry);
    }

    private String getReponseFromRawHTML(WebPage entry) throws Exception {
        String content = entry.getContent();
        String toSearch = "<input type=\"hidden\" name=\"arrSent[0]\" value=\"";
        int pos = content.indexOf(toSearch);
        if (pos == NOT_FOUND)
            throw new Exception("Cannot... nothing!");

        int begin = pos + toSearch.length();
        int end = content.indexOf("\">", begin);
        return content.substring(begin, end);
    }

    private String getQuery(String message) {
        int beginPosition = message.indexOf(' ');
        return message.substring(beginPosition);
    }

    @Override
    public boolean shouldReact(String message) {
        return message.startsWith("bashoid") && message.indexOf(' ') != NOT_FOUND;
    }

    @Override
    protected void setReaction(String message, String author) {
        try {
            String query = getQuery(message);
            String response = loadResponse(query);
            reaction.add(author + ": " + response);
        } catch (Exception e) {
            setError();
        }
    }

}
