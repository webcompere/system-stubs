package uk.org.webcompere.systemstubs.resource;

import java.util.Map;
import java.util.Properties;

/**
 * The general interface of something that can set name value pairs on itself
 * @param <T> the final type of the class which provides this
 */
@FunctionalInterface
public interface NameValuePairSetter<T extends NameValuePairSetter> {
    /**
     * Set a name value pair
     * @param name the name
     * @param value the value
     * @return <code>this</code> for fluent calling
     */
    T set(String name, String value);

    /**
     * Set many name value pairs at once
     * @param nameValuePairs an even number of inputs, whose {@link Object#toString()} is used for each name/value
     * @return <code>this</code> for fluent calling
     */
    @SuppressWarnings("unchecked")
    default T set(Object... nameValuePairs) {
        if (nameValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Must provide an even number of name/value pairs");
        }
        for (int i = 0; i < nameValuePairs.length; i += 2) {
            set(nameValuePairs[i].toString(), nameValuePairs[i + 1].toString());
        }
        return (T)this;
    }

    /**
     * Set from a collection of properties. Use with {@link PropertySource#fromFile} for example.
     * @param properties a map of values, or {@link Properties} object
     * @return <code>this</code> for fluent calling
     */
    @SuppressWarnings("unchecked")
    default T set(Map<Object, Object> properties) {
        properties.forEach((key, value) -> set(String.valueOf(key), String.valueOf(value)));
        return (T)this;
    }
}
