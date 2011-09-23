package utils;

import static utils.Constants.*;


public class XMLParser {

    public static String getSnippet(String content, String tagStart, String tagStop) throws Exception {
        return getSnippet(content, 0, tagStart, tagStop);
    }

    public static String getSnippet(String content, int startPosition, String tagStart, String tagStop) throws Exception {
        int start = content.indexOf(tagStart, startPosition);
        if (start == NOT_FOUND)
            throw new Exception("XMLParser: Cannot find tag '" + tagStart + "'.");

        start += tagStart.length();
        int stop = content.indexOf(tagStop, start);
        return (stop == NOT_FOUND) ? content.substring(start) : content.substring(start, stop);
    }

}
