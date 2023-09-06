package uk.org.webcompere.systemstubs.exception;

/**
 * When something fails to load as part of a System Stubs operation
 */
public class LoadingException extends RuntimeException {
    /**
     * Construct the Loading exception
     * @param message the message
     * @param cause the root cause
     */
    public LoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
