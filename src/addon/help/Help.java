package addon.help;

import bashoid.Addon;
import bashoid.Message;
import org.joox.Match;


public class Help extends Addon {

    private String command;
    private String[] content;

    public Help() {
        command = config.getSetting("command");

        Match addons = config.getMatch("content").children();
        content = new String[ addons.size() ];
        int index = 0;
        for ( Match addon : addons.each() )
            content[index++] = addon.first().tag() + ": " + addon.first().text();
    }

    @Override
    public boolean shouldReact(Message message) {
        return message.text.equals(command);
    }

    @Override
    protected void setReaction(Message message) {
        for(String s : content)
            sendMessage(message.author, s);
    }

}
