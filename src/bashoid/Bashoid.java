package bashoid;

import bash.Bash;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import link.Topic;
import link.Youtube;
import org.jibble.pircbot.*;
import period.PeriodicEvent;
import period.PeriodicListener;
import period.PeriodicMessage;
import utils.Config;
import utils.FloodChecker;


public class Bashoid extends PircBot implements PeriodicListener {

    private enum MessageType {UNKNOWN, BASH, STATS, MEGGIE, TOPIC, YOUTUBE};
    private PeriodicMessage periodicMessage;


    public Bashoid() {
        setName( getNickFromConfig("bashoid") );
        setAutoNickChange(true);
        setMessageDelay(0);
        trySetUTFEncoding();

        // currently not needed
        // periodicMessage = new PeriodicMessage();
        // periodicMessage.addEventListener(this);
    }

    private String getNickFromConfig(String defaultNick) {
        Config config = new Config();
        return config.getValue("nickname", defaultNick);
    }

    private void trySetUTFEncoding() {
        try {
            setEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("Cannot set UTF-8 encoding.");
        }
    }

    private MessageType getType(String sender, String message) {
        if ( Bash.isBashMessage(message) )
            return MessageType.BASH;
        else if ( message.equals(".stats") )
            return MessageType.STATS;
        else if ( sender.indexOf("meggie") > -1 && message.indexOf("co?") > -1 )
            return MessageType.MEGGIE;
        else if ( Topic.isTopicMessage(message) )
            return MessageType.TOPIC;
        else if ( Youtube.isYoutubeMessage(message) )
            return MessageType.YOUTUBE;

        return MessageType.UNKNOWN;
    }

    @Override
    public void sendPeriodicMessage(PeriodicEvent event) {
        String[] channels = getChannels();
        for (String channel : channels)
            sendMessage(channel, event.getMessage() );
    }

    @Override
    protected void onAction(String sender, String login, String hostname, String target, String action) {
        onMessage(target, sender, login, hostname, action);
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        MessageType type = getType(sender, message);
        if ( type != MessageType.UNKNOWN && FloodChecker.canBeServed(hostname) ) {
            switch (type) {
                case BASH:
                    sendBash(channel, sender);
                    break;
                case STATS:
                    sendAction(channel, "slaps " + sender + " with Ozzy Osbourne.");
                    break;
                case MEGGIE:
                    sendMessage(channel, "meggie: nic!");
                    break;
                case TOPIC:
                    sendMessage(channel, Topic.getTopicSubject(message) );
                    break;
                case YOUTUBE:
                    sendMessage(channel, Youtube.getLinkInfo(message) );
                    break;
                default:
            }
            FloodChecker.logServed(hostname);
        }
    }

    @Override
    protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        if ( recipientNick.equals( getNick() ) ) {
            joinChannel(channel);
            sendMessage(channel, Colors.BOLD + "sorry...");
        }
    }

    protected void sendListOfMessages(String target, ArrayList<String> output) {
        for ( String line : output )
            sendMessage(target, line);
    }

    protected void sendListOfNotices(String target, ArrayList<String> output) {
        for ( String line : output )
            sendNotice(target, line);
    }

    private void sendBash(String channel, String sender) {
        Bash b = new Bash();
        if ( b.errorOccured() )
            sendListOfNotices(sender, b.getOutput() );
        else
            sendListOfMessages(channel, b.getOutput() );
    }

}
