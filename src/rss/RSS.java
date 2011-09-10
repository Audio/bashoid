package rss;

import bashoid.Addon;
import utils.Config;
import java.util.ArrayList;

import static utils.Constants.*;


public class RSS extends Addon {

    private enum Cmds
    {
        CMD_INVALID,CMD_LIST,CMD_SHOW;
    };
    private static final String configKeyName = "channelName";
    private static final String configKeyUrl = "channelUrl";
    private static final String configKeyCount = "showMsgsCount";

    private ArrayList<Feed> feeds = new ArrayList<Feed>();
    private byte showMsgsCount;

    public RSS() {
        setPeriodicUpdate(60000);

        Config config = new Config("rss.xml");

        for(short i = 1; true; ++i) {
            String name = config.getValue(configKeyName + i, null);
            String url = config.getValue(configKeyUrl + i, null);
            if(name == null || url == null)
                break;
            feeds.add(new Feed(name, url));
        }

        try {
            showMsgsCount = Integer.valueOf(config.getValue(configKeyCount, "5")).byteValue();
        }
        catch(NumberFormatException e) {
            showMsgsCount = 5;
        }
    }

    private void checkFeeds() {
        String msg = null;
        for(Feed f : feeds)
        {
            try {
                msg = f.check();
                if(msg != null)
                    sendMessageToChannels(msg);
            }
            catch(Exception e) {
            }
        }
    }

    private String executeCmd(Cmds cmd, String message, String author) {
        if(feeds.isEmpty())
            return "No channels available";

        switch(cmd) {
            case CMD_LIST:
            {
                String msg = "";
                for(Feed f : feeds)
                    msg += f.getName() + " ";
                return msg;
            }
            case CMD_SHOW:
            {
                String channel;
                int index = message.indexOf(" ", message.indexOf("show"));
                int end = message.indexOf(" ", index+1);
                if(end == NOT_FOUND)
                    end = message.length();

                channel = message.substring(index+1, end);
                for(Feed f : feeds) {
                    if(channel.equalsIgnoreCase(f.getName())) {
                        try {
                            ArrayList<String> messages = f.getLastMessages(showMsgsCount);
                            sendMessage(author, "Last " +  showMsgsCount + " messages for rss channel \"" + f.getName() + "\":");
                            for(String s : messages)
                                sendMessage(author, s);
                        }
                        catch(Exception e){
                            setError();
                        }
                    }
                }
                return null;
            }
        }
        return null;
    }

    private Cmds getCommand(String message) {
        int begin = message.indexOf(' ') + 1;
        int end = message.indexOf(' ', begin);
        if(end == NOT_FOUND)
            end = message.length();

        String cmd = message.substring(begin, end);

        if     (cmd.equals("list")) return Cmds.CMD_LIST;
        else if(cmd.equals("show")) return Cmds.CMD_SHOW;
        else                        return Cmds.CMD_INVALID;
    }

    @Override
    public boolean shouldReact(String message) {
         return message.startsWith("rss") && getCommand(message) != Cmds.CMD_INVALID;
    }

    @Override
    protected void setReaction(String message, String author) {
        Cmds cmd = getCommand(message);
        if(cmd == Cmds.CMD_INVALID)
            return;
        String result = executeCmd(cmd, message, author);
        if(result != null)
            reaction.add(result);
    }

    @Override
    public void periodicAddonUpdate() {
        checkFeeds();
    }
}
