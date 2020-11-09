package uk.org.webcompere.systemstubs.properties;

import uk.org.webcompere.systemstubs.environment.PropertiesUtils;
import uk.org.webcompere.systemstubs.resource.SingularTestResource;

import java.util.Properties;

import static java.lang.System.getProperties;
import static java.lang.System.setProperties;

/**
 * Maintain system properties after a test from the ones before the test. Stores the
 * existing properties when started, and restores them when complete.
 */
public class SystemProperties extends SingularTestResource {
    private Properties originalProperties;

    @Override
    protected void doSetup() throws Exception {
        originalProperties = getProperties();
        setProperties(PropertiesUtils.copyOf(originalProperties));
    }

    @Override
    protected void doTeardown() throws Exception {
        setProperties(originalProperties);
    }
}
