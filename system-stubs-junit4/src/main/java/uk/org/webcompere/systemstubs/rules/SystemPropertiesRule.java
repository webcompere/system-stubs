package uk.org.webcompere.systemstubs.rules;

import uk.org.webcompere.systemstubs.properties.SystemProperties;
import uk.org.webcompere.systemstubs.rules.internal.SystemStubTestRule;

import java.util.Properties;

/**
 * Returns the system properties to their original state around each test block. Provides
 * the ability for properties to be prepared before the test starts, via {@link #set}.
 * @since 1.0.0
 */
public class SystemPropertiesRule extends SystemProperties implements SystemStubTestRule {

    /**
     * Default constructor provides restoration of properties
     */
    public SystemPropertiesRule() {
    }

    /**
     * Construct with some properties to apply when active
     * @param properties system properties to apply when active
     */
    public SystemPropertiesRule(Properties properties) {
        super(properties);
    }

    /**
     * Construct with a variable number of properties that will be set when the rule is active
     * @param name name of the first property
     * @param value value of the first property
     * @param nameValues pairs of name/values as Strings
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
