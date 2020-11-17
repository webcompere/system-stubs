package uk.org.webcompere.systemstubs.resource;

import uk.org.webcompere.systemstubs.ThrowingRunnable;

import java.util.concurrent.Callable;

/**
 * The execution interface. Defines the <em>execute-around</em> pattern
 * where an object can set up and tear down some sort of resource in a try-finally block
 * around calling some inner operation, returning its value.
 * @since 1.0.0
 */
@FunctionalInterface
public interface Executable {
    /**
     * Execute this test resource around a callable
     * @param callable the callable to execute
     * @param <T> the type of object to return
     * @return the result of the operation
     * @throws Exception on any error thrown by the callable
     */
    <T> T execute(Callable<T> callable) throws Exception;

    /**
     * Execute this test resource around a runnnable
     * @param runnable the runnable to execute
     * @throws Exception on any error thrown by the callable
     */
    default void execute(ThrowingRunnable runnable) throws Exception {
        execute(runnable.asCallable());
    }
}
