package uk.org.webcompere.systemstubs.rules.internal;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.webcompere.systemstubs.resource.TestResource;

import static uk.org.webcompere.systemstubs.rules.internal.Statements.toStatement;

/**
 * Common implementation to convert the {@link TestRule} interface into utilisation
 * of the {@link TestResource} interface.
 * @since 1.0.0
 */
public interface SystemStubTestRule extends TestRule, TestResource {
    @Override
    default Statement apply(Statement statement, Description description) {
        return toStatement(statement, this);
    }
}
