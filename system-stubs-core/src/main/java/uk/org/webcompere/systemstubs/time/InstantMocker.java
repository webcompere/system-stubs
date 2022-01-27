package uk.org.webcompere.systemstubs.time;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Stack;

/**
 * Used to create instant mocking
 */
@SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED",
    justification = "We need to set up the stub, but interaction is set on construction")
public class InstantMocker {
    private static final Stack<InstantOverride> OVERRIDE_STACK = new Stack<>();

    static {
        // add a mockito watcher to the now method
        Mockito.mockStatic(Instant.class, invocationOnMock -> {
            if (!invocationOnMock.getMethod().getName().equals("now")) {
                return invocationOnMock.callRealMethod();
            }
            Instant result = (Instant) invocationOnMock.callRealMethod();
            if (OVERRIDE_STACK.isEmpty()) {
                return result;
            }
            return OVERRIDE_STACK.peek().amendNow(result);
        });
    }

    /**
     * Add a new override to the stack
     * @param override the override
     */
    public static void add(InstantOverride override) {
        OVERRIDE_STACK.push(override);
    }

    /**
     * Remove the head override
     * @return <code>true</code> if there are now no more overrides and mocking has effectively stopped
     */
    public static boolean pop() {
        OVERRIDE_STACK.pop();
        return OVERRIDE_STACK.isEmpty();
    }

    /**
     * Remove a specific override
     * @param override the override to remove
     * @return <code>true</code> if it was removed
     */
    public static boolean remove(InstantOverride override) {
        return OVERRIDE_STACK.remove(override);
    }

    /**
     * Get the actual time
     * @return the actual time
     */
    public static Instant getRealTime() {
        // either we can go straight to the real time, or we can ask the current head of
        // the stack to give us the real time
        return OVERRIDE_STACK.isEmpty() ? Instant.now() : OVERRIDE_STACK.peek().getRealTime();
    }
}
