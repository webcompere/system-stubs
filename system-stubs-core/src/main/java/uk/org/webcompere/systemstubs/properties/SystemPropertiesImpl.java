package uk.org.webcompere.systemstubs.properties;

import uk.org.webcompere.systemstubs.resource.NameValuePairSetter;
import uk.org.webcompere.systemstubs.resource.SingularTestResource;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static java.lang.System.getProperties;
import static java.lang.System.setProperties;

/**
 * Maintain system properties after a test from the ones before the test. Stores the
 * existing properties when started, and restores them when complete. Allows for a list of properties
 * that will be applied to the system to be set before the stubbing is triggered.
 */
public class SystemPropertiesImpl<T extends SystemPropertiesImpl<T>> extends SingularTestResource
    implements NameValuePairSetter<T> {
    private Properties originalProperties;
    private Properties properties;

    private Set<String> propertiesToRemove = new HashSet<>();

    /**
     * Default constructor with no properties. Use {@link #set} to set properties
     * either while active or before activation.
     * @since 1.0.0
     */
    public SystemPropertiesImpl() {
        this.properties = new Properties();
    }

    /**
     * Construct with a specific set of properties.
     * @param properties properties to use
     * @since 1.0.0
     */
    public SystemPropertiesImpl(Properties properties) {
        this.properties = PropertiesUtils.copyOf(properties);
    }

    /**
     * Construct with a set of properties to apply when the object is active
     * @param name name of the first property
     * @param value value of the first property
     * @param nameValues pairs of names and values for further properties
     * @since 1.0.0
     */
    public SystemPropertiesImpl(String name, String value, String... nameValues) {
        this();
        if (nameValues.length % 2 != 0) {
            throw new IllegalArgumentException("Must have pairs of values");
        }
        properties.setProperty(name, value);
        for (int i = 0; i < nameValues.length; i += 2) {
            properties.setProperty(nameValues[i], nameValues[i + 1]);
        }
    }

    /**
     * Set a system property. If active, this will set it with {@link System#setProperty(String, String)}.
     * If not active, then this will store the property to apply when this object is part of an execution.
     * It is also possible to use {@link System#setProperty(String, String)} while this object is active,
     * but when the execution finishes, this object will be unaware of the property set, so will not set
     * it next time.
     * @param name name of the property
     * @param value value to set
     * @return this object for fluent use
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("unchecked")
    public T set(String name, String value) {
        properties.setProperty(name, value);
        if (isActive()) {
            System.setProperty(name, value);
        }
        return (T) this;
    }

    /**
     * Remove a property - this removes it from system properties if active, and remembers to remove it
     * while the object is active
     * @param name the name of the property to remove
     * @return <code>this</code> for fluent use
     * @since 2.1.5
     */
    @Override
    @SuppressWarnings("unchecked")
    public T remove(String name) {
        propertiesToRemove.add(name);
        if (isActive()) {
            System.getProperties().remove(name);
        }
        return (T) this;
    }

    @Override
    protected void doSetup() throws Exception {
        originalProperties = getProperties();
        Properties copyProperties = PropertiesUtils.copyOf(originalProperties);
        propertiesToRemove.forEach(copyProperties::remove);
        copyProperties.putAll(properties);
        setProperties(copyProperties);
    }

    @Override
    protected void doTeardown() throws Exception {
        setProperties(originalProperties);
    }
}
