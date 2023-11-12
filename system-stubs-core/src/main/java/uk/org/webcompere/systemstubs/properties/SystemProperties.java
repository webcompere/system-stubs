package uk.org.webcompere.systemstubs.properties;

import java.util.Properties;

/**
 * Maintain system properties after a test from the ones before the test. Stores the
 * existing properties when started, and restores them when complete. Allows for a list of properties
 * that will be applied to the system to be set before the stubbing is triggered.
 */
public class SystemProperties extends SystemPropertiesImpl<SystemProperties> {

    public SystemProperties() {
        super();
    }

    public SystemProperties(Properties properties) {
        super(properties);
    }

    public SystemProperties(String name, String value, String... nameValues) {
        super(name, value, nameValues);
    }
}
