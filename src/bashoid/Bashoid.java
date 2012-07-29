package bashoid;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.jibble.pircbot.*;
import utils.Config;
import utils.FloodChecker;


public class Bashoid extends PircBot implements AddonListener {

    private ArrayList<Addon> addons = new ArrayList<Addon>();

    public Bashoid() {
        setName( getNickFromConfig("bashoid") );
        setAutoNickChange(true);
        setMessageDelay(0);
        trySetUTFEncoding();
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
        Addon.setAddonListener(this);

        addons.add( new addon.bash.Bash() );
        addons.add( new addon.counter.Counter() );
        addons.add( new addon.currency.Currency() );
        addons.add( new addon.explain.Explain() );
        addons.add( new addon.help.Help() );
        addons.add( new addon.pepa.Pepa() );
        addons.add( new addon.rss.RSS() );
        // addons.add( new period.PeriodicMessage() );
        addons.add( new addon.slap.Slap() );
        addons.add( new addon.steam.Steam() );
        addons.add( new addon.stopwatch.Stopwatch() );
        addons.add( new addon.title.Title() );
        addons.add( new addon.translator.Translator() );
        addons.add( new addon.youtube.Youtube() );
    }

    @Override
    protected void onConnect()
    {
        if(addons.isEmpty())
            registerAddons();
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
        boolean canBeServed = FloodChecker.canBeServed(message.hostname);
        boolean hasReacted = false;

        for (Addon a : addons) {
            if (a.shouldReact(message) ) {
                if(!canBeServed) {
                    sendNotice(message.author, "Max requests per minute: " + FloodChecker.maxServesPerMinute() );
                    break;
                } else {
                    sendAddonOutput(a, message);
                    hasReacted = true;
                }
            }
        }

        if (hasReacted)
            FloodChecker.logServed(message.hostname);
    }

    @Override
    protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        if ( recipientNick.equals( getNick() ) ) {
            joinChannel(channel);
            sendMessage(channel, kickerNick + ": polib si");
        }
    }

    public void sendAddonAction(String target, String msg) {
        sendAction(target, msg);
    }

    public void sendAddonMessage(String target, String msg) {
        sendMessage(target, msg);
    }

    public void sendAddonMessageToChannels(String msg) {
        String[] channels = getChannels();
        for (String channel : channels)
            sendMessage(channel, msg );
    }

    private void sendAddonOutput(Addon addon, Message message) {
        List<String> reaction = addon.generateReaction(message);
        if ( addon.errorOccurred() ) {
            displayAddonFailureIfIsSet( addon.getError() );
            sendListOfNotices(message.author, reaction);
        } else {
            sendListOfMessages(message.channel, reaction);
        }
    }

    protected void sendListOfMessages(String target, List<String> output) {
        for ( String line : output )
            sendMessage(target, line);
    }

    protected void sendListOfNotices(String target, List<String> output) {
        for ( String line : output )
            sendNotice(target, line);
    }

    protected void displayAddonFailureIfIsSet(Exception e) {
        if (e != null)
            System.err.println( e.getMessage() );
    }

    public String getBotNickname() {
        return getNick();
    }

}
