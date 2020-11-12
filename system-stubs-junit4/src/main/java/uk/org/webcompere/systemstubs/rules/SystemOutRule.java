package uk.org.webcompere.systemstubs.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.webcompere.systemstubs.stream.SystemOut;
import uk.org.webcompere.systemstubs.stream.output.Output;

import java.io.OutputStream;

import static uk.org.webcompere.systemstubs.rules.internal.Statements.toStatement;

/**
 * JUnit4 test rule that captures {@link System#out} during tests.
 * @see uk.org.webcompere.systemstubs.stream.SystemOut
 */
public class SystemOutRule extends SystemOut implements TestRule {
    /**
     * Construct with an alternative {@link Output} to use
     *
     * @param output the output to write system error to
     */
    public SystemOutRule(Output<? extends OutputStream> output) {
        super(output);
    }

    /**
     * Default constructor, uses a {@link uk.org.webcompere.systemstubs.stream.output.TapStream} to
     * tap system out while the rule is active
     */
    public SystemOutRule() {
    }

    @Override
    public Statement apply(Statement statement, Description description) {
        return toStatement(statement, this);
    }
}
