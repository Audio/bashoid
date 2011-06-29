package link;

import utils.WebPage;


public class Topic {

    private static final int NOT_FOUND = -1;
    private static String URL_MASK = "http://forum.valhalla-team.com/index.php?topic=";
    private static String MESSAGE_PREFIX = "Valhalla forum: ";

    private static String loadSubject(int topicId) throws Exception {
        WebPage page = WebPage.loadWebPage("http://localhost/forum.api.php?topicId=" + topicId, "UTF-8");
        return page.getContent();
    }

    private static int getTopicId(String message) {
        int pos = message.indexOf(URL_MASK) + URL_MASK.length();
        String sub = message.substring(pos);
        return getNumbersFromBeginning(sub);
    }

    private static int getNumbersFromBeginning(String str) {
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

    public static boolean isTopicMessage(String message) {
        return message.indexOf(URL_MASK) != NOT_FOUND;
    }

    public static String getTopicSubject(String message) {
        String subject;
        try {
            int topicId = getTopicId(message);
            subject = loadSubject(topicId);
        } catch (Exception e) {
            subject = "???";
        }

        return MESSAGE_PREFIX + subject;
    }

}
