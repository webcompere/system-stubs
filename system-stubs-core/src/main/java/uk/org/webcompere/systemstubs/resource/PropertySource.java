package uk.org.webcompere.systemstubs.resource;

import uk.org.webcompere.systemstubs.exception.LoadingException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Methods to load properties from files or resources
 */
public class PropertySource {
    /**
     * Load properties from a file path
     * @param path the path to the file
     * @return a {@link Properties} object
     */
    public static Properties fromFile(Path path) {
        return fromFile(path.toFile());
    }

    /**
     * Load properties from a file path
     * @param path the path to the file
     * @return a {@link Properties} object
     */
    public static Properties fromFile(String path) {
        return fromFile(Paths.get(path));
    }

    /**
     * Load properties from a file
     * @param file the file
     * @return a {@link Properties} object
     */
    public static Properties fromFile(File file) {
        try {
            return fromInputStream(new FileInputStream(file));
        } catch (IOException e) {
            throw new LoadingException("Cannot read file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Load properties from a resource
     * @param resourceName the resource to load
     * @return a {@link Properties} object
     */
    public static Properties fromResource(String resourceName) {
        return fromInputStream(PropertySource.class.getClassLoader().getResourceAsStream(resourceName));
    }

    /**
     * Load properties from a given input stream. Note, the subclasses of
     * {@link uk.org.webcompere.systemstubs.stream.input.AltInputStream} like
     * {@link uk.org.webcompere.systemstubs.stream.input.LinesAltStream} may be a useful way to programmatically
     * set properties from lines in the <code>name=value</code> syntax used by properties files.
     * @param stream the stream to load
     * @return a {@link Properties} objects
     */
    public static Properties fromInputStream(InputStream stream) {
        try (InputStream read = stream) {
            Properties properties = new Properties();
            properties.load(read);
            return properties;
        } catch (IOException e) {
            throw new LoadingException("Could not load values", e);
        }
    }
}
