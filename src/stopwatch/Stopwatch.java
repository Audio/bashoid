package stopwatch;

import bashoid.MessageListener;
import bashoid.Addon;
import java.util.ArrayList;

import static utils.Constants.*;


public class Stopwatch extends Addon {

    private static final byte CMD_INVALID         = -1;
    private static final byte CMD_START_STOPWATCH = 1;
    private static final byte CMD_STOP_STOPWATCH  = 2;
    private static final byte CMD_GET_STOPWATCH   = 3;
    private static final byte CMD_SET_TIMER       = 4;
    private static final byte CMD_REMOVE_TIMER    = 5;

    private static final short HOUR               = 3600;
    private static final short MINUTE             = 60;

    private long    stopwatchTime;
    private boolean stopwatchRunning;
    private String  stopwatchAuthor;
    private ArrayList<StopwatchTimer> timers = new ArrayList<StopwatchTimer>();

    public Stopwatch(MessageListener listener) {
        super(listener);
        stopwatchRunning = false;
    }

    private String executeCmd(byte cmd, String message, String author)
    {
        switch(cmd)
        {
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
                for(StopwatchTimer t : timers)
                {
                    if(author.equals(t.getAuth()))
                        return "You already have one timer";
                }
                int index = message.indexOf(' ');
                if(index == -1)
                     return "You need to set time";
                index = message.indexOf(' ', index+1)+1;

                long time = parseTime(message, index);
                if(time == 0)
                    return "You need to set time in correct form (eg. 4h10m5s)";

                long period = MINUTE;
                index = message.indexOf(' ', index);
                if(index != -1)
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
                for(StopwatchTimer t : tmp)
                {
                    if(author.equals(t.getAuth()))
                    {
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

    private void startUpdate()
    {
        if(periodicAddonUpdate == null)
            setPeriodicUpdate(1000);
    }

    private void stopUpdate()
    {
        if(periodicAddonUpdate != null && !stopwatchRunning && timers.size() == 0)
            stopPeriodicUpdate();
    }

    public static String timeToString(long time)
    {
        String result = "";
        if(time < MINUTE)
            result += time + "s";
        else
        {
            if(time/MINUTE < MINUTE)
            {
                result += (long)Math.floor(time/MINUTE) + "m";
                if(dv(time,MINUTE) != 0)
                    result += " " + dv(time,MINUTE) + "s";
            }
            else
            {
                result += (long)Math.floor(time/HOUR) + "h";
                if(dv(time,HOUR) >= MINUTE)
                    result += " " + (long)Math.floor(dv(time,3600)/MINUTE) + "m";

                if(dv(time,MINUTE) != 0)
                    result += " " + dv(time,MINUTE) + "s";
            }
        }
        
        return result;
    }

    private long parseTime(String message, int index)
    {
        long result = 0;
        int end = message.indexOf(' ', index);
        if(end == -1)
            end = message.length();

        String time = message.substring(index, end);
        try{
            result = Integer.decode(time);
            return result;
        }
        catch(Exception e){ }

        String tmp = "";
        char ch;
        for(byte i = 0; i < time.length(); ++i)
        {
            ch = time.charAt(i);
            if(tmp.length() != 0)
            {
                short mul = -1;
                if     (ch == 'h' || ch == 'H') mul = HOUR;
                else if(ch == 'm' || ch == 'M') mul = MINUTE;
                else if(ch == 's' || ch == 'S') mul = 1;
                if(mul != -1)
                {
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

    public static long dv(long num, long div)
    {
        return num - ((long)Math.floor(num/div)*div);
    }

    private byte getCommand(String message)
    {
        int begin = message.indexOf(' ') + 1;
        int end = message.indexOf(' ', begin);
        if(end == -1)
            end = message.length();
        String cmd = message.substring(begin, end);

        if(cmd.equals("start"))
            return CMD_START_STOPWATCH;
        else if(cmd.equals("stop"))
            return CMD_STOP_STOPWATCH;
        else if(cmd.equals("get"))
            return CMD_GET_STOPWATCH;
        else if(cmd.equals("timer"))
        {
            begin = message.indexOf(' ', end);
            if(begin == -1)
                return CMD_INVALID;
            return CMD_SET_TIMER;
        }
        else if(cmd.equals("rmtimer"))
            return CMD_REMOVE_TIMER;
        return CMD_INVALID;
    }

    @Override
    public boolean shouldReact(String message) {
         return message.startsWith("stopwatch") && message.indexOf(' ') != NOT_FOUND;
    }

    @Override
    protected void setReaction(String message, String author) {
        try {
            byte cmd = getCommand(message);
            if(cmd == CMD_INVALID)
                return;
            String result = executeCmd(cmd, message, author);
            if(result != null)
                reaction.add(result);
        } catch (Exception e) {
            // System.err.println(e); --debug
            setError();
        }
    }

    @Override
    public void periodicAddonUpdate()
    {
        if(stopwatchRunning)
            ++stopwatchTime;

        ArrayList<StopwatchTimer> tmp = (ArrayList<StopwatchTimer>)timers.clone();
        for(StopwatchTimer t : tmp)
        {
            if(!t.update())
            {
                timers.remove(timers.indexOf(t));
                stopUpdate();
            }
            if(t.getResponse() != null)
                sendMessage(t.getAuth(), t.getResponse());
        }
    }

}
