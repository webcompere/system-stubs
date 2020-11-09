package uk.org.webcompere.systemstubs.stream;

/**
 * Thrown when there is an error substituting a system stream
 */
public class StreamException extends RuntimeException {
    public StreamException(String message, Throwable cause) {
        super(message, cause);
    }
}
