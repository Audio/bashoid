package period;

import java.util.EventObject;


public class PeriodicEvent extends EventObject {

    private String message;


    public PeriodicEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
