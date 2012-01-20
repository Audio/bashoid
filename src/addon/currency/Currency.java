package addon.currency;

import bashoid.Message;
import bashoid.Addon;
import java.net.URLEncoder;
import java.text.ParseException;
import utils.*;


public class Currency extends Addon {

    private static final String SERVICE_URL = "http://www.google.com/ig/calculator?hl=en&q=";


    private String loadResponse(String query) throws Exception {
        String url = SERVICE_URL + URLEncoder.encode(query, "UTF-8");
        WebPage page = WebPage.loadWebPage(url, "UTF-8");
        return getResponseFromJSON(page);
    }

    private String getResponseFromJSON(WebPage entry) throws ParseException {
        String[] p = entry.getContent().split(",");
        return ( p[2].split("\"").length > 1 ) ? "Asi incorrect syntax nebo co." : p[0].split("\"")[1] + " = " + p[1].split("\"")[1];
    }

    @Override
    public boolean shouldReact(Message message) {
        String[] parts = message.text.split(" ");
        try {
            Float.parseFloat(parts[0]);
        } catch (NumberFormatException e) {
            return false;
        }
        return parts.length == 4 && parts[2].equals("to");
    }

    @Override
    protected void setReaction(Message message) {
        try {
            reaction.add( loadResponse(message.text) );
        } catch (ParseException pe) {
            setError("Cannot parse output.", pe);
        } catch (Exception e) {
            setError("Cannot load given URL.", e);
        }
    }

}
