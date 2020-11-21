package uk.org.webcompere.systemstubs.exception;

/**
 * When something fails to load as part of a System Stubs operation
 */
public class LoadingException extends RuntimeException {
    public LoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
