package utils;

import java.util.HashMap;
import java.util.Map;


public class Formatter {

    private static HashMap<String, String> tagList;

    static {
        tagList = new HashMap<String, String>();
        tagList.put("&lt;",   "<");
        tagList.put("&gt;",   ">");
        tagList.put("&quot;", "\"");
        tagList.put("&apos;", "'");
        tagList.put("&#039;", "'");
        tagList.put("&nbsp;", " ");
        tagList.put("&amp;",  "&");
        tagList.put("<br />",  "");
        tagList.put("<span style=\"color: ;\">", "");
        tagList.put("</span>", "");
    }
    
    public static String removeHTML(String htmlToPlain) {
        for ( Map.Entry<String, String> entry : tagList.entrySet() )
            htmlToPlain = htmlToPlain.replace( entry.getKey(), entry.getValue() );

        return htmlToPlain;
    }

}
