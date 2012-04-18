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
        //bot.connect(server);
        //bot.joinChannel(channel);

        connectCheckTimer = new Timer();
        checkTask = new connectedCheckTask();
        connectCheckTimer.scheduleAtFixedRate(checkTask, CONNECT_CHECK_TIME, CONNECT_CHECK_TIME);
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

            if(!Arrays.asList(bot.getChannels()).contains(channel)) {
                bot.joinChannel(channel);
            }
        }
    }

}
