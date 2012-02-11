package bashoid;

import java.util.ArrayList;
import java.util.TimerTask;


public class PeriodicAddonTask extends TimerTask {
    private ArrayList<PeriodicAddonListener> listeners = new ArrayList<PeriodicAddonListener>();

    public void addEventListener(PeriodicAddonListener pl) {
        listeners.add(pl);
    }

    @Override
    public void run() {
        for (int i = 0; i < listeners.size(); ++i)
            listeners.get(i).periodicAddonUpdate();
    }

    public void clear() {
        listeners.clear();
    }
}
