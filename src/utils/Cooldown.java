package utils;


public class Cooldown {

    private long seconds;
    private long whenStarted;
    private final long NEVER = 0;

    public Cooldown(long seconds) {
        this.seconds = seconds;
        this.whenStarted = 0;
    }

    public void start() {
        whenStarted = currentTime();
    }

    public void stop() {
        whenStarted = NEVER;
    }

    public boolean isActive() {
        setInactiveIfExpired();
        return whenStarted != NEVER;
    }

    private long currentTime() {
        return System.currentTimeMillis() / 1000L;
    }

    private void setInactiveIfExpired() {
        if (whenStarted != NEVER && timeElapsed() > seconds)
            whenStarted = NEVER;
    }

    private long timeElapsed() {
        return currentTime() - whenStarted;
    }

    public long remainingSeconds() {
        if ( !isActive() )
            return 0;

        return whenStarted + seconds - currentTime();
    }

    public long nextCooldownFromNow() {
        return currentTime() + seconds;
    }

}
