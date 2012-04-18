package bashoid;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import utils.Config;


public class BashoidMain {

    private static final long CONNECT_CHECK_TIME = 5000;

    private static String server;
    private static String channel;
    private static Timer connectCheckTimer;
    private static Bashoid bot;
    private static connectedCheckTask checkTask;

    public static void main(String[] args) throws Exception {
        loadValuesFromConfig();
        runBot();
    }

    private static void loadValuesFromConfig() {
        Config config = new Config();
        server = config.getValue("server", "irc.rizon.net");
        channel = config.getValue("channel", "#abraka");
    }

    private static void runBot() throws Exception {
        bot = new Bashoid();

        connectCheckTimer = new Timer();
        checkTask = new connectedCheckTask();
        connectCheckTimer.scheduleAtFixedRate(checkTask, 0, CONNECT_CHECK_TIME);
    }

    private static class connectedCheckTask extends TimerTask {
        @Override
        public void run() {
            if(!bot.isConnected()) {
                try {
                    bot.connect(server);
                } catch(Exception unused) {
                    return;
                }
            }

            // Bashoid can only connect to one channel,
            // so we don't have to check channel's name
            if(bot.getChannels().length == 0) {
                bot.joinChannel(channel);
            }
        }
    }

}
