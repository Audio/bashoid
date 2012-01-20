package addon.help;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import bashoid.Addon;
import bashoid.Message;
import utils.Config;


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
    public boolean shouldReact(Message message) {
        return message.text.equals(helpCmd);
    }

    @Override
    protected void setReaction(Message message) {
        for(String s : help)
            sendMessage(message.author, s);
    }
}
