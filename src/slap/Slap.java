package slap;

import bashoid.Addon;
import bashoid.Message;


public class Slap extends Addon {

    private static final String MESSAGE_PREFIX = "slaps ";
    private static final double PROBABILITY_TO_SLAP = 0.02;


    @Override
    public boolean shouldReact(Message message) {
        return message.text.startsWith(MESSAGE_PREFIX)
            && message.text.length() > MESSAGE_PREFIX.length() + 1
            && Math.random() <= PROBABILITY_TO_SLAP;
    }

    @Override
    protected void setReaction(Message message) {
        sendAction(message.channel, "slaps " + message.author + " around a bit with a large trout");
    }

}
