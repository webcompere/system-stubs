package uk.org.webcompere.systemstubs.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.webcompere.systemstubs.stream.SystemErr;
import uk.org.webcompere.systemstubs.stream.output.Output;

import java.io.OutputStream;

import static uk.org.webcompere.systemstubs.rules.internal.Statements.toStatement;

/**
 * JUnit4 test rule that captures {@link System#err} during tests.
 * @see uk.org.webcompere.systemstubs.stream.SystemErr
 */
public class SystemErrRule extends SystemErr implements TestRule {
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

    @Override
    public Statement apply(Statement statement, Description description) {
        return toStatement(statement, this);
    }
}
