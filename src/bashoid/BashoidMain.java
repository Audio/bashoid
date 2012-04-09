package bashoid;

import utils.Config;


public class BashoidMain {

    private static String server;
    private static String channel;


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
        Bashoid bot = new Bashoid();
        bot.connect(server);
        bot.joinChannel(channel);
    }

}
