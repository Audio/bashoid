package bashoid;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.jibble.pircbot.*;
import period.PeriodicEvent;
import period.PeriodicListener;
import period.PeriodicMessage;
import utils.Config;
import utils.FloodChecker;


public class Bashoid extends PircBot implements PeriodicListener {

    private PeriodicMessage periodicMessage;
    private ArrayList<Addon> addons = new ArrayList<Addon>();


    public Bashoid() {
        setName( getNickFromConfig("bashoid") );
        setAutoNickChange(true);
        setMessageDelay(0);
        trySetUTFEncoding();
        registerAddons();

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

    private void registerAddons() {
        addons.add( new bash.Bash() );
        addons.add( new pepa.Pepa() );
        addons.add( new topic.Topic() );
        addons.add( new youtube.Youtube() );
        addons.add( new translator.Translator() );
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
        onMessage( new Message(channel, sender, login, hostname, message) );
    }

    protected void onMessage(Message message) {
        if ( !FloodChecker.canBeServed(message.hostname) ) {
            sendNotice(message.author, "Prekrocen maximalni pocet pozadavku za minutu: " + FloodChecker.maxServesPerMinute() );
        } else {

            boolean hasReacted = false;

            for (Addon a : addons) {
                if ( a.shouldReact(message.text) ) {
                    sendAddonOutput(a, message);
                    hasReacted = true;
                }
            }

            if (hasReacted)
                FloodChecker.logServed(message.hostname);

        }

    }

    @Override
    protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        if ( recipientNick.equals( getNick() ) ) {
            joinChannel(channel);
            sendMessage(channel, kickerNick + ": polib si");
        }
    }

    private void sendAddonOutput(Addon addon, Message message) {
        List<String> reaction = addon.generateReaction(message.text, message.author);
        if ( addon.errorOccurred() )
            sendListOfNotices(message.author, reaction);
        else
            sendListOfMessages(message.channel, reaction);
    }

    protected void sendListOfMessages(String target, List<String> output) {
        for ( String line : output )
            sendMessage(target, line);
    }

    protected void sendListOfNotices(String target, List<String> output) {
        for ( String line : output )
            sendNotice(target, line);
    }

}
