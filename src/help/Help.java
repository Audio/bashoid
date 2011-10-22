package help;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import bashoid.Addon;
import utils.Config;

import static utils.Constants.*;


public class Help extends Addon {
    private ArrayList<String> help = new ArrayList<String>();
    private String helpCmd;

    public Help() {
        Config config = new Config();
        helpCmd = config.getValue("helpCommand", "!help");
        loadHelp();
    }

    private void loadHelp() {
        try {
            BufferedReader in = new BufferedReader(new FileReader("help.txt"));
            String strLine;
            while ((strLine = in.readLine()) != null) {
                help.add(strLine);
            }
            in.close();
        }
        catch(Exception e) {
        }
    }

    @Override
    public boolean shouldReact(String message) {
        return message.equals(helpCmd);
    }

    @Override
    protected void setReaction(String message, String author) {
        for(String s : help)
            sendMessage(author, s);
    }
}
