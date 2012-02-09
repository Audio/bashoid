package addon.help;

import bashoid.Addon;
import bashoid.Message;


public class Help extends Addon {

    private String command;
    private String[] content;

    public Help() {
        command = config.getSetting("helpCommand");
        content = config.getValue("content").trim().split("\n");
    }

    @Override
    public boolean shouldReact(Message message) {
        return message.text.equals(command);
    }

    @Override
    protected void setReaction(Message message) {
        for(String s : content)
            sendMessage(message.author, s.trim() );
    }

}
