package bashoid;

import java.util.ArrayList;
import java.util.List;


public abstract class Addon implements PeriodicAddonListener {

    protected List<String> reaction = new ArrayList<String>();
    protected boolean errorOccurred;
    protected AddonConfig config = new AddonConfig( getClass().getName() );
    protected PeriodicAddonUpdate periodicAddonUpdate;
    protected Exception error;

    protected static AddonListener addonListener;
    protected final static String MESSAGE_FAIL = "Addon has failed.";


    public abstract boolean  shouldReact(Message message);
    protected abstract void  setReaction(Message message);

    public static void setAddonListener(AddonListener listener) {
        addonListener = listener;
    }

    public final List<String> generateReaction(Message message) {
        resetAddonStatus();
        setReaction(message);
        return reaction;
    }

    protected final void resetAddonStatus() {
        reaction.clear();
        errorOccurred = false;
        error = null;
    }

    protected final void setError() {
        setError(MESSAGE_FAIL, null);
    }

    protected final void setError(Exception e) {
        setError(MESSAGE_FAIL, e);
    }

    protected final void setError(String reason) {
        setError(reason, null);
    }

    protected final void setError(String reason, Exception e) {
        reaction.clear();
        reaction.add(reason);
        errorOccurred = true;
        error = e;
    }

    public final Exception getError() {
        return error;
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

    public void periodicAddonUpdate() {
    }

    protected void sendAction(String target, String msg) {
        addonListener.sendAddonAction(target, msg);
    }

    protected void sendMessage(String target, String msg) {
        addonListener.sendAddonMessage(target, msg);
    }

    protected void sendMessageToChannels(String msg) {
        addonListener.sendAddonMessageToChannels(msg);
    }

    protected void reloadConfig() {
        config.reload();
    }

}
