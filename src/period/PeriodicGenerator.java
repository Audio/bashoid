package period;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;


public class PeriodicGenerator extends TimerTask {

    private ArrayList<PeriodicListener> listeners = new ArrayList();


    public void addEventListener(PeriodicListener pl) {
        listeners.add(pl);
    }

    @Override
    public void run() {
        String message = "Hey, I'm still alive!";
        PeriodicEvent event = new PeriodicEvent(this, message);
        for(PeriodicListener l : listeners)
            l.sendPeriodicMessage(event);
    }

}
