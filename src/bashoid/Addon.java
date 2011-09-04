package bashoid;

import java.util.ArrayList;
import java.util.List;

public abstract class Addon implements PeriodicAddonListener {

    protected List<String> reaction = new ArrayList<String>();
    protected boolean errorOccurred;
    protected MessageListener messageListener;
    protected PeriodicAddonUpdate periodicAddonUpdate;

    protected final static String MESSAGE_FAIL = "Addon has failed.";


    public abstract boolean  shouldReact(String message);
    protected abstract void  setReaction(String message, String author);

    public Addon(MessageListener listener) {
        messageListener = listener;
    }

    public final List<String> generateReaction(String message, String author) {
        reaction.clear();
        errorOccurred = false;
        setReaction(message, author);
        return reaction;
    }

    protected final void setError() {
        setError(MESSAGE_FAIL);
    }

    protected final void setError(String reason) {
        reaction.clear();
        reaction.add(reason);
        errorOccurred = true;
    }

    public final boolean errorOccurred() {
        return errorOccurred;
    }

    protected void setPeriodicUpdate(long period) {
        periodicAddonUpdate = new PeriodicAddonUpdate(period);
        periodicAddonUpdate.addEventListener((PeriodicAddonListener)this);
    }

    protected void stopPeriodicUpdate() {
        periodicAddonUpdate.stop();
        periodicAddonUpdate = null;
    }

    @Override
    public void periodicAddonUpdate() {
    }

    protected void sendMessage(String target, String msg) {
        messageListener.sendMessageListener(target, msg);
    }

    protected void sendMessageToChannels(String msg) {
        messageListener.sendMessageToChannelsListener(msg);
    }
}
