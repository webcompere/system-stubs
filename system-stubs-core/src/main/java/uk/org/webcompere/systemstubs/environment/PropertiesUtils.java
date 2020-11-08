package uk.org.webcompere.systemstubs.environment;

import java.util.Properties;

public class PropertiesUtils {
	public static Properties copyOf(
			Properties source
	) {
		Properties copy = new Properties();
		copy.putAll(source);
		return copy;
	}
}
