package pepa;

import bashoid.Addon;
import bashoid.Message;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.WebPage;

import static utils.Constants.*;


public class Pepa extends Addon {

    private HashMap<String, String> formValues = new HashMap<String, String>();

    private static final String ENCODING = "windows-1250";
    private static final String LAST_RESPONSE_KEY = "arrSent[0]";


    private String loadResponse(String query) throws Exception {
        String postData = generatePostData(query);
        WebPage entry = WebPage.loadWebPage("http://pepa.vyskup.com/index.php", ENCODING, postData);
        updateFormValues(entry);
        return getLastResponse();
    }

    private String generatePostData(String query) throws UnsupportedEncodingException {
        String postData = "strText=" + URLEncoder.encode(query, ENCODING);
        for (Map.Entry<String, String> formValue : formValues.entrySet() ) {
            String key = formValue.getKey();
            String value = formValue.getValue();
            postData += "&" + key + "=" + URLEncoder.encode(value, ENCODING);
        }
        return postData;
    }

    private void updateFormValues(WebPage entry) throws ParseException {
        formValues.clear();
        Element form = Jsoup.parse( entry.getContent() ).getElementsByTag("form").first();
        Elements inputs = form.getElementsByAttributeValue("type", "hidden");
        for (Element input : inputs)
            formValues.put( input.attr("name"), input.val() );
    }

    private String getLastResponse() throws Exception {
        if ( !formValues.containsKey(LAST_RESPONSE_KEY) )
            throw new Exception("Last response was not found.");

        return formValues.get(LAST_RESPONSE_KEY);
    }

    private String getQuery(String message) {
        int beginPosition = message.indexOf(' ');
        return message.substring(beginPosition);
    }

    @Override
    public boolean shouldReact(Message message) {
        String myNick = addonListener.getBotNickname();
        return message.text.startsWith(myNick) && message.text.indexOf(' ') != NOT_FOUND;
    }

    @Override
    protected void setReaction(Message message) {
        try {
            String query = getQuery(message.text);
            String response = loadResponse(query);
            reaction.add(message.author + ": " + response);
        } catch (Exception e) {
            setError(e);
        }
    }

}
