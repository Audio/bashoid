package utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


public class WebPage {

    private String content;
    private String url;

    private WebPage(String url, String content) {
        this.url = url;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public static WebPage loadWebPage(String address) throws Exception {
        URL url = new URL(address);
        InputStream is = url.openStream();
        BufferedReader reader = new BufferedReader( new InputStreamReader(is) );

        String line, lines = "";
        while (( line = reader.readLine() ) != null)
            lines += line + "\n";

        is.close();
        return new WebPage(address, lines);
    }

}
