package slap;

import bashoid.Addon;


public class Slap extends Addon {

    private static final String MESSAGE_PREFIX = "slaps ";
    private static final double PROBABILITY_TO_SLAP = 0.1;


    @Override
    public boolean shouldReact(String message) {
        return message.startsWith(MESSAGE_PREFIX)
            && message.length() > MESSAGE_PREFIX.length() + 1
            && Math.random() <= PROBABILITY_TO_SLAP;
    }

    @Override
    protected void setReaction(String message, String author) {
        sendAction(author, "slaps " + author);
    }

}
