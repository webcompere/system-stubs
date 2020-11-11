package uk.org.webcompere.systemstubs.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.webcompere.systemstubs.security.SystemExit;

import static uk.org.webcompere.systemstubs.rules.internal.Statements.toStatement;

/**
 * Add to a test to catch System exit events
 */
public class SystemExitRule extends SystemExit implements TestRule {
    @Override
    public Statement apply(Statement statement, Description description) {
        return toStatement(statement, this);
    }
}
