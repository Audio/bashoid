package addon.counter;

import bashoid.Message;
import bashoid.Addon;
import java.util.HashMap;
import java.util.Map;

public class Counter extends Addon {

    private HashMap<String, Integer> counters;

    public Counter() {
        counters = new HashMap<String, Integer>();
    }

    private String executeCmd(String cmd, String author) {
        if(cmd.equals("counterget")) {
            if(counters.containsKey(author))
                return author + "'s counter value is " + counters.get(author);
            else
                return author + " has no counter";
        }

        int num = 0;
        int currentVal = 0;
        try {
            num = getNum(cmd);
        }
        catch(NumberFormatException ex) {
            return null;
        }

        if(counters.containsKey(author))
            currentVal = counters.get(author);

        currentVal += num;
        counters.put(author, (Integer)currentVal);
        return null;
    }

    private int getNum(String text) throws NumberFormatException {
        int res = 0;
        if(text.startsWith("+="))
            res = Integer.parseInt(text.substring(2));
        else if(text.startsWith("-="))
            res = Integer.parseInt(text.substring(2)) * -1;
        else if(text.equals("++"))
            res = 1;
        else if(text.equals("--"))
            res = -1;
        else
            throw new NumberFormatException();
        return res;
    }

    @Override
    public boolean shouldReact(Message message) {
        if(message.text.equals("counter get"))
            return true;

        try {
            getNum(message.text.replaceAll(" ", ""));
            return true;
        }
        catch(NumberFormatException ex) {
            return false;
        }
    }

    @Override
    protected void setReaction(Message message) {
        String result = executeCmd(message.text.replaceAll(" ", ""), message.author);
        if(result != null)
            reaction.add(result);
    }

}
