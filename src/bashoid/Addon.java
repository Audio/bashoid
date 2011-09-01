package bashoid;

import java.util.ArrayList;
import java.util.List;


public abstract class Addon {

    protected List<String> reaction = new ArrayList<String>();
    protected boolean errorOccured;

    public abstract boolean  shouldReact(String message);
    protected abstract void  setReaction(String message, String author);
    public abstract boolean  errorOccurred();

    public final List<String> generateReaction(String message, String author) {
        reaction.clear();
        setReaction(message, author);
        return reaction;
    }

}
