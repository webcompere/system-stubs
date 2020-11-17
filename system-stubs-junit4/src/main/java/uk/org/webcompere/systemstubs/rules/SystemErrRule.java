package uk.org.webcompere.systemstubs.rules;

import uk.org.webcompere.systemstubs.rules.internal.SystemStubTestRule;
import uk.org.webcompere.systemstubs.stream.SystemErr;
import uk.org.webcompere.systemstubs.stream.output.Output;

import java.io.OutputStream;

/**
 * JUnit4 test rule that captures {@link System#err} during tests.
 * @see uk.org.webcompere.systemstubs.stream.SystemErr
 * @since 1.0.0
 */
public class SystemErrRule extends SystemErr implements SystemStubTestRule {
    /**
     * Construct with an alternative {@link Output} to use
     * @param output the output to write system error to
     */
    public SystemErrRule(Output<? extends OutputStream> output) {
        super(output);
    }

    /**
     * Default constructor, uses a {@link uk.org.webcompere.systemstubs.stream.output.TapStream} to
     * tap system error while the rule is active
     */
    public SystemErrRule() {
    }
}
