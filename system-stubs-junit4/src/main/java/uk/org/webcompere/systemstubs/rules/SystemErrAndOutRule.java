package uk.org.webcompere.systemstubs.rules;

import uk.org.webcompere.systemstubs.rules.internal.SystemStubTestRule;
import uk.org.webcompere.systemstubs.stream.SystemErrAndOut;
import uk.org.webcompere.systemstubs.stream.output.Output;

/**
 * JUnit4 rule for tapping both {@link System#err} and {@link System#out} at the same
 * time with the same {@link Output}
 * @since 1.0.0
 */
public class SystemErrAndOutRule extends SystemErrAndOut implements SystemStubTestRule {
    /**
     * Default constructor - uses a {@link uk.org.webcompere.systemstubs.stream.output.TapStream}
     */
    public SystemErrAndOutRule() {
    }

    /**
     * Construct with a shared {@link Output}
     * @param output the output both system err and out will write to
     */
    public SystemErrAndOutRule(Output output) {
        super(output);
    }
}
