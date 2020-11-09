package uk.org.webcompere.systemstubs;

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
}
