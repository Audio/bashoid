package utils;

import java.util.HashMap;
import java.util.Map;

import static utils.Constants.*;


public class Formatter {

    private static HashMap<String, String> tagList;

    static {
        tagList = new HashMap<String, String>();
        tagList.put("&lt;",   "<");
        tagList.put("&gt;",   ">");
        tagList.put("&quot;", "\"");
        tagList.put("&apos;", "'");
        tagList.put("&nbsp;", " ");
        tagList.put("&amp;",  "&");
        tagList.put("<br />",  "");
        tagList.put("<span style=\"color: ;\">", "");
        tagList.put("</span>", "");
    }
    
    public static String removeHTML(String htmlToPlain) {
        for ( Map.Entry<String, String> entry : tagList.entrySet() )
            htmlToPlain = htmlToPlain.replace( entry.getKey(), entry.getValue() );

        int index = 0;
        int indexEnd = 0;

        while(true) {
            boolean found = false;
            while(!found) {
                index = htmlToPlain.indexOf("&#", indexEnd);
                if(index == NOT_FOUND)
                    break;

                indexEnd = htmlToPlain.indexOf(";", index);

                if(indexEnd == NOT_FOUND)
                    break;

                if(indexEnd - index > 5)
                    continue;

                found = true;
            }
            if(!found)
                break;

            try{
                char ascii = (char)Integer.valueOf(htmlToPlain.substring(index+2, indexEnd)).byteValue();
                htmlToPlain = htmlToPlain.replaceAll(htmlToPlain.substring(index, indexEnd+1), Character.toString(ascii));
            }
            catch(NumberFormatException e){
            }
        }

        return htmlToPlain;
    }

}
