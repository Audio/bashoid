package bashoid;

import java.util.ArrayList;
import java.util.List;


public abstract class Addon {

    protected List<String> reaction = new ArrayList<String>();
    protected boolean errorOccured;

    protected final static String MESSAGE_FAIL = "Addon has failed.";


    public abstract boolean  shouldReact(String message);
    protected abstract void  setReaction(String message, String author);

    public final List<String> generateReaction(String message, String author) {
        reaction.clear();
        setReaction(message, author);
        return reaction;
    }

    protected final void setError() {
        setError(MESSAGE_FAIL);
    }

    protected final void setError(String reason) {
        reaction.clear();
        reaction.add(reason);
        errorOccured = true;
    }

    public final boolean errorOccurred() {
        return errorOccured;
    }

}
