package addon.stopwatch;

import bashoid.Message;
import bashoid.Addon;
import java.util.ArrayList;

import static utils.Constants.*;


public class Stopwatch extends Addon {

    private enum Cmds {
        CMD_INVALID, CMD_START_STOPWATCH, CMD_STOP_STOPWATCH, CMD_GET_STOPWATCH, CMD_SET_TIMER , CMD_REMOVE_TIMER;
    };

    private static final short HOUR               = 3600;
    private static final short MINUTE             = 60;

    private long    stopwatchTime;
    private boolean stopwatchRunning;
    private String  stopwatchAuthor;
    private ArrayList<StopwatchTimer> timers = new ArrayList<StopwatchTimer>();


    public Stopwatch() {
        stopwatchRunning = false;
    }

    private String executeCmd(Cmds cmd, String message, String author) {
        switch(cmd) {
            case CMD_START_STOPWATCH:
                if(stopwatchRunning)
                    return "Stopwatch is already running";
                stopwatchRunning = true;
                stopwatchTime = 0;
                stopwatchAuthor = author;
                startUpdate();
                return "Stopwatch started";
            case CMD_STOP_STOPWATCH:
            {
                if(!stopwatchRunning)
                    return "Stopwatch is not running";
                if(!stopwatchAuthor.equals(author))
                    return "You can't stop " + stopwatchAuthor + "'s stopwatch";
                stopwatchRunning = false;
                stopUpdate();
                String result = "Stopwatch stopped at " + timeToString(stopwatchTime);
                stopwatchTime = 0;
                return result;
            }
            case CMD_GET_STOPWATCH:
            {
                if(!stopwatchRunning)
                    return "Stopwatch is not running";
                String result = "Stopwatch is at " + timeToString(stopwatchTime);
                return result;
            }
            case CMD_SET_TIMER:
            {
                for(StopwatchTimer t : timers) {
                    if(author.equals(t.getAuth()))
                        return "You already have one timer";
                }
                int index = message.indexOf(' ');
                if(index == NOT_FOUND)
                     return "You need to set time";
                index = message.indexOf(' ', index+1)+1;

                long time = parseTime(message, index);
                if(time == 0)
                    return "You need to set time in correct form (eg. 4h10m5s)";

                long period = MINUTE;
                index = message.indexOf(' ', index);
                if(index != NOT_FOUND)
                    period = parseTime(message, index+1);
                StopwatchTimer timer = new StopwatchTimer(author, time, period);
                timers.add(timer);
                startUpdate();
                return "Timer for " + time + " seconds started for " + author;
            }
            case CMD_REMOVE_TIMER:
            {
                StopwatchTimer tm = null;
                ArrayList<StopwatchTimer> tmp = (ArrayList<StopwatchTimer>)timers.clone();
                for(StopwatchTimer t : tmp) {
                    if(author.equals(t.getAuth())) {
                        tm = t;
                        timers.remove(timers.indexOf(t));
                    }
                }
                if(tm == null)
                    return "You don't have timer";
                stopUpdate();
                return author + "'s timer for " + timeToString(tm.getBaseTime()) + " removed";
            }
        }
        return null;
    }

    private void startUpdate() {
        if(periodicAddonUpdate == null)
            setPeriodicUpdate(1000);
    }

    private void stopUpdate() {
        if(periodicAddonUpdate != null && !stopwatchRunning && timers.size() == 0)
            stopPeriodicUpdate();
    }

    public static String timeToString(long time) {
        String result = "";
        if(time < MINUTE)
            result += time + "s";
        else {
            if(time/MINUTE < MINUTE) {
                result += (long)Math.floor(time/MINUTE) + "m";
                if(dv(time,MINUTE) != 0)
                    result += " " + dv(time,MINUTE) + "s";
            } else {
                result += (long)Math.floor(time/HOUR) + "h";
                if(dv(time,HOUR) >= MINUTE)
                    result += " " + (long)Math.floor(dv(time,3600)/MINUTE) + "m";

                if(dv(time,MINUTE) != 0)
                    result += " " + dv(time,MINUTE) + "s";
            }
        }

        return result;
    }

    private long parseTime(String message, int index) {
        long result = 0;
        int end = message.indexOf(' ', index);
        if(end == NOT_FOUND)
            end = message.length();

        String time = message.substring(index, end);
        try{
            result = Integer.decode(time);
            return result;
        }
        catch(Exception e){ }

        String tmp = "";
        char ch;
        for(byte i = 0; i < time.length(); ++i) {
            ch = time.charAt(i);
            if(tmp.length() != 0) {
                short mul = NOT_FOUND;
                if     (ch == 'h' || ch == 'H') mul = HOUR;
                else if(ch == 'm' || ch == 'M') mul = MINUTE;
                else if(ch == 's' || ch == 'S') mul = 1;
                if(mul != NOT_FOUND) {
                    try{
                        result += Integer.decode(tmp)*mul;
                    }
                    catch(Exception e){
                        return 0;
                    }
                    tmp = "";
                    continue;
                }
            }
            tmp += ch;
        }
        return result < 0 ? 0 : result;
    }

    public static long dv(long num, long div) {
        return num - ((long)Math.floor(num/div)*div);
    }

    private Cmds getCommand(String message) {
        int begin = message.indexOf(' ') + 1;
        int end = message.indexOf(' ', begin);
        if(end == NOT_FOUND)
            end = message.length();
        String cmd = message.substring(begin, end);

        if(cmd.equals("start"))
            return Cmds.CMD_START_STOPWATCH;
        else if(cmd.equals("stop"))
            return Cmds.CMD_STOP_STOPWATCH;
        else if(cmd.equals("get"))
            return Cmds.CMD_GET_STOPWATCH;
        else if(cmd.equals("timer")) {
            begin = message.indexOf(' ', end);
            if(begin == NOT_FOUND)
                return Cmds.CMD_INVALID;
            return Cmds.CMD_SET_TIMER;
        }
        else if(cmd.equals("rmtimer"))
            return Cmds.CMD_REMOVE_TIMER;
        return Cmds.CMD_INVALID;
    }

    @Override
    public boolean shouldReact(Message message) {
         return message.text.startsWith("stopwatch") && message.text.indexOf(' ') != NOT_FOUND;
    }

    @Override
    protected void setReaction(Message message) {
        Cmds cmd = getCommand(message.text);
        if(cmd == Cmds.CMD_INVALID)
            return;
        String result = executeCmd(cmd, message.text, message.author);
        if(result != null)
            reaction.add(result);
    }

    @Override
    public void periodicAddonUpdate() {
        if(stopwatchRunning)
            ++stopwatchTime;

        ArrayList<StopwatchTimer> tmp = (ArrayList<StopwatchTimer>)timers.clone();
        for(StopwatchTimer t : tmp) {
            if(!t.update()) {
                timers.remove(timers.indexOf(t));
                stopUpdate();
            }
            if(t.getResponse() != null)
                sendMessage(t.getAuth(), t.getResponse());
        }
    }

}
