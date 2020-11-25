package uk.org.webcompere.systemstubs.stream.output;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * A target for output. This can be plugged into a {@link uk.org.webcompere.systemstubs.stream.SystemStreamBase}
 * derived class to be converted to a {@link java.io.PrintStream} for use with <code>System.out</code>
 * etc.
 * @since 1.0.0
 */
public interface Output<T extends OutputStream> {
    /**
     * Convert an existing {@link OutputStream} into an {@link Output}. Providing no
     * ability to clear or get the text of that output stream.
     * @param stream the target stream
     * @param <S> the type of stream
     * @return an {@link Output} object
     */
    static <S extends OutputStream> Output<S> fromStream(S stream) {
        return () -> stream;
    }

    /**
     * Convert an existing {@link OutputStream} into an {@link Output}. Providing no
     * ability to clear or get the text of that output stream.
     * @param stream the target stream
     * @param <S> the type of stream
     * @return an {@link Output} object
     */
    static <S extends OutputStream> Output<S> fromCloseableStream(S stream) {
        return new Output<S>() {

            @Override
            public void closeOutput() throws Exception {
                stream.close();
            }

            @Override
            public S getOutputStream() {
                return stream;
            }
        };
    }

    /**
     * Get the plaintext
     * @return the output as a single string - not null
     */
    default String getText() {
        return "";
    }

    /**
     * Clear the output fresh for another test
     */
    default void clear() {
        // does nothing
    }

    /**
     * When the target stream is meant to be closed, then close it
     */
    default void closeOutput() throws Exception {
        // does nothing here
    }

    /**
     * Access the output stream that's behind this
     * @return get the output stream - can be <code>null</code> if the object hasn't been activated
     */
    T getOutputStream();

    /**
     * Access the current output object
     * @return this if an {@link Output} object, or the child {@link Output} otherwise
     */
    default Output<T> getOutput() {
        return this;
    }

    /**
     * Get the plain text broken into lines by the system's line separator
     * @return a stream of lines
     */
    default Stream<String> getLines() {
        return Arrays.stream(getText().split(Pattern.quote(System.lineSeparator())));
    }

    /**
     * Get the plain text broken into lines and recombined with <code>\n</code>
     * @return the output as a single string
     */
    default String getLinesNormalized() {
        return getLinesNormalized("\n");
    }

    /**
     * Get the plain text broken into lines and recombined with a custom delimited
     * @param linebreak the linebreak delimiter
     * @return the output as a single string
     */
    default String getLinesNormalized(String linebreak) {
        String combined = getLines().collect(joining(linebreak));
        if (combined.isEmpty()) {
            return "";
        }
        // the split process removes a trailing linebreak/implied end linebreak
        return combined + linebreak;
    }

    /**
     * Convert this {@link Output} into a factory which does nothing except return this
     * @return a factory
     */
    default OutputFactory<T> factoryOfSelf() {
        return original -> this;
    }
}
