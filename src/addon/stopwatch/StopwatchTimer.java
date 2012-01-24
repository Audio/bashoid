package addon.stopwatch;


public class StopwatchTimer {

    private String author;
    private String response;
    private long baseTime;
    private long remainingTime;
    private long period;


    public StopwatchTimer(String author, long time, long period) {
        this.author = author;
        baseTime = time;
        remainingTime = time;
        if(period != 0 && baseTime > period)
            this.period = period;
        else if(baseTime > 60)
            this.period = 60;
    }

    public String getAuth() {
        return author;
    }

    public String getResponse() {
        return response;
    }

    public long getBaseTime() {
        return baseTime;
    }

    public boolean update() {
        response = null;
        --remainingTime;
        if(remainingTime <= 0) {
            response = "Removing timer, " + Stopwatch.timeToString(baseTime) + " has elapsed";
            return false;
        }
        if(period != 0 && Stopwatch.dv(remainingTime, period) == 0)
            response = "Remaining time: " + Stopwatch.timeToString(remainingTime);
        return true;
    }

}