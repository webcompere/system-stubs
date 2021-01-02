package uk.org.webcompere.systemstubs.rules;

import uk.org.webcompere.systemstubs.rules.internal.SystemStubTestRule;
import uk.org.webcompere.systemstubs.stream.SystemOut;
import uk.org.webcompere.systemstubs.stream.output.Output;
import uk.org.webcompere.systemstubs.stream.output.OutputFactory;

import java.io.OutputStream;

/**
 * JUnit4 test rule that captures {@link System#out} during tests.
 * @see uk.org.webcompere.systemstubs.stream.SystemOut
 * @since 1.0.0
 */
public class SystemOutRule extends SystemOut implements SystemStubTestRule {
    /**
     * Construct with an alternative {@link Output} to use
     *
     * @param output the output to write system error to
     */
    public SystemOutRule(Output<? extends OutputStream> output) {
        super(output);
    }

    /**
     * Construct with a {@link OutputFactory} for creating output objects from the existing output
     *
     * @param outputFactory the factory to use
     */
    public SystemOutRule(OutputFactory<? extends OutputStream> outputFactory) {
        super(outputFactory);
    }

    /**
     * Default constructor, uses a {@link uk.org.webcompere.systemstubs.stream.output.TapStream} to
     * tap system out while the rule is active
     */
    public SystemOutRule() {
    }
}
