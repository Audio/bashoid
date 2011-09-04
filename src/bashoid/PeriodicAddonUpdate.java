package bashoid;

import java.util.Timer;


public final class PeriodicAddonUpdate {

    private Timer timer;
    private PeriodicAddonGenerator generator;

    public PeriodicAddonUpdate(long period) {
        generator = new PeriodicAddonGenerator();
        timer = new Timer();
        timer.scheduleAtFixedRate(generator, period, period);
    }

    public void addEventListener(PeriodicAddonListener pl) {
        generator.addEventListener(pl);
    }

    public void stop()
    {
        timer.cancel();
        timer = null;
        generator.clear();
        generator = null;
    }

}
