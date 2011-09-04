package period;

import bashoid.Addon;
import bashoid.MessageListener;


public final class PeriodicMessage extends Addon {

    private static final long TWENTY_MINUTES = 20 * 60 * 1000;


    public PeriodicMessage(MessageListener listener) {
        super(listener);
        setPeriodicUpdate(TWENTY_MINUTES);
    }

    @Override
    public boolean shouldReact(String message) {
        return false;
    }

    @Override
    protected void setReaction(String message, String author) {
    }

    @Override
    public void periodicAddonUpdate() {
        sendMessageToChannels("Hey, I'm still alive!");
    }

}
