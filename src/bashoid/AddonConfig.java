package bashoid;

import java.io.File;
import org.joox.Match;

import static org.joox.JOOX.$;


public class AddonConfig {

    private Match rootElement;


    public AddonConfig(String classname) {
        try {
            rootElement = $( new File(classname + ".xml") );
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
