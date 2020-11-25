package uk.org.webcompere.systemstubs.stream.output;

import java.io.OutputStream;

/**
 * Functional interface for creating {@link Output} objects for use as the output
 * based on the current system {@link java.io.PrintStream}
 * @param <T> the type of stream the {@link Output} will handle
 */
@FunctionalInterface
public interface OutputFactory<T extends OutputStream> {
    /**
     * Current the current input stream, produce an {@link Output}
     * @param original the original stream
     * @return an output to use for writing
     * @throws Exception on error creating the object
     */
    Output<T> apply(OutputStream original) throws Exception;
}
