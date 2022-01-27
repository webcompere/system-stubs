package uk.org.webcompere.systemstubs.time;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;

public class FixedInstantOverride extends InstantOverride {
    private final TemporalAccessor sourceTime;

    public FixedInstantOverride(TemporalAccessor sourceTime) {
        this.sourceTime = sourceTime;
    }

    /**
     * Overridden to perform override specific logic
     *
     * @param realNow the current time
     * @return the time we want it to be
     */
    @Override
    protected Instant doAmendNow(Instant realNow) {
        return Instant.from(sourceTime);
    }
}
