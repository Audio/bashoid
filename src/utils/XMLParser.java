package utils;

import java.text.ParseException;

import static utils.Constants.*;


public class XMLParser {

    protected static int nextOccurrenceIndex;


    public static int getNextOccurrenceIndex() {
        return nextOccurrenceIndex;
    }

    public static String getSnippet(String content, String tagStart, String tagStop) throws ParseException {
        return getSnippet(content, 0, tagStart, tagStop);
    }

    public static String getSnippet(String content, int startPosition, String tagStart, String tagStop) throws ParseException {
        int start = content.indexOf(tagStart, startPosition);
        if (start == NOT_FOUND)
            throw new ParseException("XMLParser: Cannot find tag '" + tagStart + "'.", startPosition);

        start += tagStart.length();
        int stop = content.indexOf(tagStop, start);
        nextOccurrenceIndex = stop + tagStop.length();
        return (stop == NOT_FOUND) ? content.substring(start) : content.substring(start, stop);
    }

}
