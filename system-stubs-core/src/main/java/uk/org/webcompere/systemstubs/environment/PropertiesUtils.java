package uk.org.webcompere.systemstubs.environment;

import java.util.Properties;

public class PropertiesUtils {
    /**
     * Produce a clone of some properties in a new object
     * @param source the source to clone
     * @return a distinct copy
     */
    public static Properties copyOf(Properties source) {
        Properties copy = new Properties();
        copy.putAll(source);
        return copy;
    }
}
