package topic;

import bashoid.Addon;
import utils.WebPage;

import static utils.Constants.*;


public class Topic extends Addon {

    private static final String URL_MASK = "http://forum.valhalla-team.com/index.php?topic=";
    private static final String MESSAGE_PREFIX = "Valhalla forum: ";

    private String loadSubject(int topicId) throws Exception {
        WebPage page = WebPage.loadWebPage("http://valhalla-team.com/forum.api.php?topicId=" + topicId, "UTF-8");
        return page.getContent();
    }

    private int getTopicId(String message) {
        int pos = message.indexOf(URL_MASK) + URL_MASK.length();
        String sub = message.substring(pos);
        return getNumbersFromBeginning(sub);
    }

    private int getNumbersFromBeginning(String str) {
        int length = str.length();
        char[] input = str.toCharArray();
        String output = "";
        for (int i = 0; i < length; ++i) {
            int val = Character.getNumericValue( input[i] );
            if (val < 0 || val > 9)
                break;
            else
                output += input[i];
        }

        return Integer.parseInt(output);
    }

    @Override
    public boolean shouldReact(String message) {
        return message.indexOf(URL_MASK) != NOT_FOUND;
    }

    @Override
    protected void setReaction(String message, String author) {
        try {
            int topicId = getTopicId(message);
            reaction.add(MESSAGE_PREFIX + loadSubject(topicId) );
        } catch (Exception e) {
            setError();
        }
    }

}
