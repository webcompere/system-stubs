package uk.org.webcompere.systemstubs.rules;

import uk.org.webcompere.systemstubs.properties.SystemProperties;
import uk.org.webcompere.systemstubs.rules.internal.SystemStubTestRule;

import java.util.Properties;

/**
 * Returns the system properties to their original state around each test block.
 */
public class SystemPropertiesRule extends SystemProperties implements SystemStubTestRule {

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
    public SystemPropertiesRule set(String name, String value) {
        return (SystemPropertiesRule)super.set(name, value);
    }
}
