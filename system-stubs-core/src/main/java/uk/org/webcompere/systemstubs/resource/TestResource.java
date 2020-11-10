package uk.org.webcompere.systemstubs.resource;

import uk.org.webcompere.systemstubs.ThrowingRunnable;

import java.util.concurrent.Callable;

/**
 * A test resource is something that can be set up at the start of a test and
 * torn down at the end.
 */
public interface TestResource {
    /**
     * Prepare the resource for testing
     * @throws Exception on error starting
     */
    void setup() throws Exception;

    /**
     * Clean up the resource
     * @throws Exception on error cleaning up
     */
    void teardown() throws Exception;

    /**
     * Execute this test resource around a callable
     * @param callable the callable to execute
     * @param <T> the type of object to return
     * @return the result of the operation
     * @throws Exception on any error thrown by the callable
     */
    default <T> T execute(Callable<T> callable) throws Exception {
        return Resources.execute(callable, this);
    }

    /**
     * Execute this test resource around a runnnable
     * @param runnable the runnable to execute
     * @throws Exception on any error thrown by the callable
     */
    default void execute(ThrowingRunnable runnable) throws Exception {
        execute(runnable.asCallable());
    }
}
