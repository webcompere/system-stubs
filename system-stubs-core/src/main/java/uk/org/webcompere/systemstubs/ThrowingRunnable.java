package uk.org.webcompere.systemstubs;

import uk.org.webcompere.systemstubs.exception.WrappedThrowable;

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
    void run() throws Throwable;

    /**
     * Convert this to a Callable
     * @return a {@link Callable} which executes this
     */
    default Callable<Void> asCallable() {
        return () -> {
          try {
              run();
          } catch (Error | Exception e) {
              throw e;
          } catch (Throwable t) {
              throw new WrappedThrowable(t);
          }
          return null;
        };
    }

    /**
     * Convert a lambda of type runnable to Callable
     * @param runnable a runnable that can be converted
     * @return a {@link Callable}
     */
    static Callable<Void> asCallable(ThrowingRunnable runnable) {
        return runnable.asCallable();
    }
}
