package bashoid;import bash.Bash;import java.util.ArrayList;import org.jibble.pircbot.*;public class Bashoid extends PircBot {        public Bashoid() {        setName("bashoid");        setAutoNickChange(true);    }    @Override    protected void onMessage(String channel, String sender, String login, String hostname, String message) {        if ( Bash.isBashMessage(message) )            sendListOfMessages(channel, new Bash().getOutput() );    }    @Override    protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {        joinChannel(channel);        sendMessage(channel, Colors.BOLD + "sorry...");    }    protected void sendListOfMessages(String target, ArrayList<String> output) {        for ( String line : output )            sendMessage(target, line);    }}