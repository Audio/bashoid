package addon.currency;

import bashoid.Message;
import java.util.regex.Pattern;


public class CzechCurrency extends Currency {

    private static final char EURO = '\u20AC';
    private static final Pattern TO_CZK_PATTERN = Pattern.compile("^\\d+((\\.|,)\\d+)? +(usd|eur|euros|\\$|" + EURO + ")$", Pattern.CASE_INSENSITIVE);


    @Override
    public boolean shouldReact(Message message) {
        return TO_CZK_PATTERN.matcher(message.text).find();
    }

    @Override
    protected void setReaction(Message message) {
        message.text += " to czk";
        super.setReaction(message);
    }

}
