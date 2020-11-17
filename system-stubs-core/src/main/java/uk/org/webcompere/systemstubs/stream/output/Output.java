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
     * Access the output stream that's behind this
     * @return get the output stream
     */
    T getOutputStream();

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
}
