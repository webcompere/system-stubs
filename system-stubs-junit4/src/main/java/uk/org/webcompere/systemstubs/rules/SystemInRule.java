package uk.org.webcompere.systemstubs.rules;

import uk.org.webcompere.systemstubs.rules.internal.SystemStubTestRule;
import uk.org.webcompere.systemstubs.stream.SystemIn;
import uk.org.webcompere.systemstubs.stream.alt.AltInputStream;

import java.io.InputStream;

/**
 * The {@link SystemIn} system stub as a JUnit 4 test rule
 */
public class SystemInRule extends SystemIn implements SystemStubTestRule {
    /**
     * Construct with multiple lines on System.in
     * @param lines lines to provide - will be separated by system line separator
     */
    public SystemInRule(String... lines) {
        super(lines);
    }

    /**
     * Construct with a single text block
     * @param text text to provide - no change in formatting
     */
    public SystemInRule(String text) {
        super(text);
    }

    /**
     * Construct with an input stream to read from - this will be closed on tidy up
     * @param inputStream the stream to read from
     */
    public SystemInRule(InputStream inputStream) {
        super(inputStream);
    }

    /**
     * Construct with any of the {@link AltInputStream} objects - e.g.
     * {@link uk.org.webcompere.systemstubs.stream.alt.LinesAltStream}
     * @param altInputStream the stream to use while the rule is active
     */
    public SystemInRule(AltInputStream altInputStream) {
        super(altInputStream);
    }
}
