package uk.org.webcompere.systemstubs.properties;

import java.util.Map;
import java.util.Properties;

import static java.util.stream.Collectors.toMap;

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

    /**
     * Convert a properties object to a map
     * @param properties the source properties
     * @return a <code>Map</code>
     */
    public static Map<String, String> toStringMap(Properties properties) {
        return properties.entrySet()
            .stream()
            .collect(toMap(entry -> String.valueOf(entry.getKey()),
                entry -> String.valueOf(entry.getValue())));
    }
}
