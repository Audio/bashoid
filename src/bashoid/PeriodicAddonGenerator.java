package bashoid;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;


public class PeriodicAddonGenerator extends TimerTask {
    private ArrayList<PeriodicAddonListener> listeners = new ArrayList();

    public void addEventListener(PeriodicAddonListener pl) {
        listeners.add(pl);
    }

    @Override
    public void run() {
        for(PeriodicAddonListener l : listeners)
            l.periodicAddonUpdate();
    }

    public void clear() {
        listeners.clear();
    }
}
