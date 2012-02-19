package bashoid;

import java.io.File;
import org.joox.Match;

import static org.joox.JOOX.$;


public class AddonConfig {

    private String filename;
    private Match rootElement;


    public AddonConfig(String classname) {
        filename = classname + ".xml";
        reload();
    }

    protected void reload() {
        try {
            rootElement = $( new File(filename) );
        } catch (Exception e) {
            // config file doesn't exist
        }
    }

    public String getSetting(String key) {
        return getValue("settings " + key);
    }

    public String getValue(String path) {
        return rootElement.find(path).text();
    }

    public Match getMatch(String path) {
        return rootElement.find(path);
    }

}
