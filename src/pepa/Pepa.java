package pepa;

import bashoid.Addon;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import utils.WebPage;
import utils.XMLParser;

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

    private void updateFormValues(WebPage entry) throws Exception {
        String content = XMLParser.getSnippet( entry.getContent(), "<form", "</form>");
        formValues.clear();

        String rawTag;
        for(int i = 0; true; i = XMLParser.getNextOccurrenceIndex() ) {
            try {
                rawTag = XMLParser.getSnippet(content, i, "<input type=\"hidden", ">");
            } catch (Exception e) {
                break;
            }

            HiddenTag tag = new HiddenTag(rawTag);
            formValues.put( tag.getName(), tag.getValue() );
        }
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
    public boolean shouldReact(String message) {
        String myNick = addonListener.getBotNickname();
        return message.startsWith(myNick) && message.indexOf(' ') != NOT_FOUND;
    }

    @Override
    protected void setReaction(String message, String author) {
        try {
            String query = getQuery(message);
            String response = loadResponse(query);
            reaction.add(author + ": " + response);
        } catch (Exception e) {
            setError(e);
        }
    }

}
