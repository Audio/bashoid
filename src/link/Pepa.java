package link;

import java.net.URLEncoder;
import utils.WebPage;

import static utils.Constants.*;


public class Pepa {


    private static String loadResponse(String query) throws Exception {
        String postData = URLEncoder.encode("strText", "windows-1250") + "=" + URLEncoder.encode(query, "windows-1250");
        WebPage entry = WebPage.loadWebPage("http://pepa.vyskup.com/index.php", "windows-1250", postData);
        return getReponseFromRawHTML(entry);
    }

    private static String getReponseFromRawHTML(WebPage entry) throws Exception {
        String content = entry.getContent();
        String toSearch = "<input type=\"hidden\" name=\"arrSent[0]\" value=\"";
        int pos = content.indexOf(toSearch);
        if (pos == NOT_FOUND)
            throw new Exception("Cannot find video title in the XML source.");

        int begin = pos + toSearch.length();
        int end = content.indexOf("\">", begin);
        return content.substring(begin, end);
    }

    private static String getQuery(String message) {
        int beginPosition = message.indexOf(' ');
        return message.substring(beginPosition);
    }

    public static boolean isPepaMessage(String message) {
        return message.startsWith("bashoid") && message.indexOf(' ') != NOT_FOUND;
    }

    public static String getResponse(String message, String author) {
        try {
            String query = getQuery(message);
            String response = loadResponse(query);
            return author + ": " + response;
        } catch (Exception e) {
            System.out.println( e.getMessage() );
            return author + ": java.NevimCoNatoRictException";
        }
    }

}
