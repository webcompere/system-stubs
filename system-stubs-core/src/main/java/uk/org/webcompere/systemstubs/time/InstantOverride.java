package uk.org.webcompere.systemstubs.time;

import java.time.Instant;

/**
 * Base class for time overriding. Overrides should be immutable and should either implement
 * equals and hashcode, or should reasonably be recognisable as equal/hashcoded using object reference
 */
public abstract class InstantOverride {
    private boolean useRealTime = false;

    /**
     * Has the logic for real-time override
     * @param realNow the time from the actual base method
     * @return the {@link Instant} to use in its place
     */
    public final Instant amendNow(Instant realNow) {
        if (useRealTime) {
            return realNow;
        }

        return doAmendNow(realNow);
    }

    /**
     * Forces a get of the real time
     * @return the real time
     */
    public Instant getRealTime() {
        boolean previousUseRealTime = useRealTime;
        useRealTime = true;
        Instant now = Instant.now();
        useRealTime = previousUseRealTime;
        return now;
    }

    /**
     * Overridden to perform override specific logic
     * @param realNow the current time
     * @return the time we want it to be
     */
    protected abstract Instant doAmendNow(Instant realNow);

    /**
     * Is this override allowing the real time to pass through?
     * @return <code>true</code> for bypass
     */
    public boolean isUseRealTime() {
        return useRealTime;
    }

    /**
     * Set bypass mode on
     * @param useRealTime true to allow bypassing
     */
    public void setUseRealTime(boolean useRealTime) {
        this.useRealTime = useRealTime;
    }
}
