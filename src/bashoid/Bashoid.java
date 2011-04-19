package bashoid;

import bash.Bash;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import link.Youtube;
import org.jibble.pircbot.*;
import period.PeriodicEvent;
import period.PeriodicListener;
import period.PeriodicMessage;
import utils.Config;
import utils.FloodChecker;


public class Bashoid extends PircBot implements PeriodicListener {

    private enum MessageType {UNKNOWN, BASH, YOUTUBE, STATS};
    private PeriodicMessage periodicMessage;


    public Bashoid() {
        setName( getNickFromConfig("bashoid") );
        setAutoNickChange(true);
        setMessageDelay(0);
        trySetUTFEncoding();

        periodicMessage = new PeriodicMessage();
        periodicMessage.addEventListener(this);
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

    private MessageType getType(String message) {
        if ( Bash.isBashMessage(message) )
            return MessageType.BASH;
        else if ( Youtube.isYoutubeMessage(message) )
            return MessageType.YOUTUBE;
        else if ( message.equals(".stats") )
            return MessageType.STATS;

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
        Youtube.setVideoIDIfPresent(action, sender);
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        MessageType type = getType(message);
        if ( type != MessageType.UNKNOWN && FloodChecker.canBeServed(hostname) ) {
            switch (type) {
                case BASH:
                    sendBash(channel, sender);
                    break;
                case YOUTUBE:
                    sendMessage(channel, Youtube.getLastUsedLinkInfo() );
                    break;
                case STATS:
                    sendAction(channel, "slaps " + sender + " with Ozzy Osbourne.");
                    break;
                default:
            }
            FloodChecker.logServed(hostname);
        }

        Youtube.setVideoIDIfPresent(message, sender);
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
