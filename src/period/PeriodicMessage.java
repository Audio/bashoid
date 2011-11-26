package period;

import bashoid.Addon;
import bashoid.Message;


public final class PeriodicMessage extends Addon {

    private static final long TWENTY_MINUTES = 20 * 60 * 1000;


    public PeriodicMessage() {
        setPeriodicUpdate(TWENTY_MINUTES);
    }

    @Override
    public boolean shouldReact(Message message) {
        return false;
    }

    @Override
    protected void setReaction(Message message) {
    }

    @Override
    public void periodicAddonUpdate() {
        sendMessageToChannels("Hey, I'm still alive!");
    }

}
