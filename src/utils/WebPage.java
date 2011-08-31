package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;


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

    public static WebPage loadWebPage(String address, String encoding) throws Exception {
        return loadWebPage(address, encoding, null);
    }

    public static WebPage loadWebPage(String address, String encoding, String postData) throws Exception {
        URL url = new URL(address);
        URLConnection conn = url.openConnection();

        if (postData != null)
            writePostData(postData, conn);

        String response = readResponse(conn, encoding);
        return new WebPage(address, response);
    }

    private static void writePostData(String postData, URLConnection connection) throws IOException {
        connection.setDoOutput(true);
        OutputStreamWriter writter = new OutputStreamWriter( connection.getOutputStream() );
        writter.write(postData);
        writter.flush();
        writter.close();
    }

    private static String readResponse(URLConnection connection, String encoding) throws IOException {
        BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream(), encoding) );

        String line, lines = "";
        while (( line = reader.readLine() ) != null)
            lines += line + "\n";

        reader.close();
        return lines;
    }

}
