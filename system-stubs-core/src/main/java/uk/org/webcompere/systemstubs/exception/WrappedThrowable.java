package uk.org.webcompere.systemstubs.exception;

/**
 * Wrapper to help pass a throwable out through a {@link java.util.concurrent.Callable}
 */
public class WrappedThrowable extends RuntimeException {
    public WrappedThrowable(Throwable cause) {
        super(cause);
    }
}
