package uk.org.webcompere.systemstubs.resource;

import org.junit.jupiter.api.Test;
import uk.org.webcompere.systemstubs.stream.input.LinesAltStream;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.systemstubs.resource.PropertySource.fromFile;
import static uk.org.webcompere.systemstubs.resource.PropertySource.fromResource;

class PropertySourceTest {
    // this is a disgusting way to initialize a map - sorry! Java's `Map.of` and Guava's `ImmutableMap.of`
    // are much better!
    private static final Map<String, String> EXPECTED_TEST_PROPERTIES = new HashMap<String, String>() {{
            put("value1", "foo");
            put("value2", "bar");
        }};


    @Test
    void fromFileByFilename() {
        Properties props = fromFile("src/test/resources/test.properties");
        assertThat(props).containsAllEntriesOf(EXPECTED_TEST_PROPERTIES);
    }

    @Test
    void fromFileByFile() {
        Properties props = fromFile(new File("src/test/resources/test.properties"));
        assertThat(props).containsAllEntriesOf(EXPECTED_TEST_PROPERTIES);
    }

    @Test
    void fromFileByPath() {
        Properties props = fromFile(new File("src/test/resources/test.properties").toPath());
        assertThat(props).containsAllEntriesOf(EXPECTED_TEST_PROPERTIES);
    }

    @Test
    void fromResources() {
        Properties props = fromResource("test.properties");
        assertThat(props).containsAllEntriesOf(EXPECTED_TEST_PROPERTIES);
    }

    @Test
    void fromLines() {
        Properties props = PropertySource.fromInputStream(new LinesAltStream("value1=foo", "value2=bar"));
        assertThat(props).containsAllEntriesOf(EXPECTED_TEST_PROPERTIES);
    }
}
