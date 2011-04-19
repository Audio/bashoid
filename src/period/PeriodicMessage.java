package period;

import java.util.Timer;


public final class PeriodicMessage {

    private Timer timer;
    private PeriodicGenerator generator;
    private final long TWENTY_MINUTES = 20 * 60 * 1000;


    public PeriodicMessage() {
        generator = new PeriodicGenerator();
        timer = new Timer();
        timer.scheduleAtFixedRate(generator, TWENTY_MINUTES, TWENTY_MINUTES);
    }

    public void addEventListener(PeriodicListener pl) {
        generator.addEventListener(pl);
    }

}
