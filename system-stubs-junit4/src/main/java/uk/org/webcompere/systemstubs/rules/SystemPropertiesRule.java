package uk.org.webcompere.systemstubs.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

import java.util.Properties;

import static uk.org.webcompere.systemstubs.rules.internal.Statements.toStatement;

/**
 * Returns the system properties to their original state around each test block.
 */
public class SystemPropertiesRule extends SystemProperties implements TestRule {

    /**
     * {@inheritDoc}
     */
    public SystemPropertiesRule() {
    }

    /**
     * {@inheritDoc}
     */
    public SystemPropertiesRule(Properties properties) {
        super(properties);
    }

    /**
     * {@inheritDoc}
     */
    public SystemPropertiesRule(String name, String value, String... nameValues) {
        super(name, value, nameValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement apply(Statement statement, Description description) {
        return toStatement(statement, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SystemPropertiesRule set(String name, String value) {
        return (SystemPropertiesRule)super.set(name, value);
    }
}
