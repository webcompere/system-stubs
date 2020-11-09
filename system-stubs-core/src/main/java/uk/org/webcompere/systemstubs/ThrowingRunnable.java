package uk.org.webcompere.systemstubs;

import java.util.concurrent.Callable;

/**
 * Code that should be executed by on of the methods of {@link SystemStubs}.
 * This code may throw an {@link Exception}. Therefore we cannot use
 * {@link Runnable}.
 */
public interface ThrowingRunnable {
    /**
     * Execute the action.
     *
     * @throws Exception the action may throw an arbitrary exception.
     */
    void run() throws Exception;

    /**
     * Convert to a Callable
     * @return a callable which executes this
     */
    default Callable<Void> asCallable() {
        return () -> {
          run();
          return null;
        };
    }
}
