package uk.org.webcompere.systemstubs.time;

import uk.org.webcompere.systemstubs.resource.SingularTestResource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;

/**
 * Controls {@link Instant#now}
 */
public class Now extends SingularTestResource {
    private InstantOverride override;

    public Now() {
    }

    /**
     * Regardless of mocking what's the actual time
     * @return the actual time
     */
    public Instant getRealTime() {
        return InstantMocker.getRealTime();
    }

    /**
     * Set the current time to the given time
     * @param temporalAccessor the time to set
     * @return this for fluent calling
     */
    public Now fixed(TemporalAccessor temporalAccessor) {
        substitute(new FixedInstantOverride(temporalAccessor));
        return this;
    }

    @Override
    protected void doSetup() {
        if (override != null) {
            InstantMocker.add(override);
        }
    }

    @Override
    protected void doTeardown() {
        if (override != null) {
            InstantMocker.remove(override);
        }
    }

    /**
     * Change the current instant override
     * @param override the instant override to set
     */
    private void substitute(InstantOverride override) {
        if (isActive()) {
            doTeardown();
        }
        this.override = override;
        if (isActive()) {
            doSetup();
        }
    }
}
