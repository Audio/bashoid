package utils;

import java.io.FileInputStream;
import java.util.Properties;


public class Config {

    private String filename;
    private Properties properties;

    private static final String DEFAULT_FILENAME = "config.xml";


    public Config() {
        this(DEFAULT_FILENAME);
    }

    public Config(String filename) {
        this.filename = filename;
        if (properties == null)
            loadFromFile();
    }

    private void loadFromFile() {
        try {
            FileInputStream file = new FileInputStream(filename);
            properties = new Properties();
            properties.loadFromXML(file);
            file.close();
        } catch (Exception e) {
            System.err.println( e.getMessage() );
        }
    }

    public String getValue(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

}
