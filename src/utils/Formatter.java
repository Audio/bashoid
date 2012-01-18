package utils;

import org.jsoup.Jsoup;


public class Formatter {

    public static String removeHTML(String htmlToPlain) {
        return Jsoup.parse(htmlToPlain).text();
    }

}
