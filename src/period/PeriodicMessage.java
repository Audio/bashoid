package period;

import bashoid.Addon;


public final class PeriodicMessage extends Addon {

    private static final long TWENTY_MINUTES = 20 * 60 * 1000;


    public PeriodicMessage() {
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
