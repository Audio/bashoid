package utils;


public class Cooldown {

    private long seconds;
    private long whenStarted;
    private final long NEVER = 0;

    public Cooldown(long seconds) throws IllegalArgumentException {
        if (seconds < 1)
            throw new IllegalArgumentException("Number of seconds must be greater than zero.");

        this.seconds = seconds;
        this.whenStarted = 0;
    }

    public long length() {
        return seconds;
    }

    public void start() {
        whenStarted = CurrentTime.inSeconds();
    }

    public void stop() {
        whenStarted = NEVER;
    }

    public boolean isActive() {
        setInactiveIfExpired();
        return whenStarted != NEVER;
    }

    private void setInactiveIfExpired() {
        if (whenStarted != NEVER && timeElapsed() > seconds)
            whenStarted = NEVER;
    }

    private long timeElapsed() {
        return CurrentTime.inSeconds() - whenStarted;
    }

    public long remainingSeconds() {
        if ( !isActive() )
            return 0;

        return whenStarted + seconds - CurrentTime.inSeconds();
    }

    public long nextCooldownFromNow() {
        return CurrentTime.inSeconds() + seconds;
    }

}
