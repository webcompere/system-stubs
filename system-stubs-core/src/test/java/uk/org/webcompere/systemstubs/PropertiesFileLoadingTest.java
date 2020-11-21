package uk.org.webcompere.systemstubs;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.systemstubs.resource.PropertySource.fromResource;

public class PropertiesFileLoadingTest {
    @Nested
    class ForEnvironmentVariables {
        @Test
        void can_load_properties_from_resources() throws Exception {
            new EnvironmentVariables()
                .set(fromResource("test.properties"))
                .execute(() -> {
                   assertThat(System.getenv("value1")).isEqualTo("foo");
                   assertThat(System.getenv("value2")).isEqualTo("bar");
                });
        }
    }

    @Nested
    class ForSystemProperties {
        @Test
        void can_load_properties_from_resources() throws Exception {
            new SystemProperties()
                .set(fromResource("test.properties"))
                .execute(() -> {
                    assertThat(System.getProperty("value1")).isEqualTo("foo");
                    assertThat(System.getProperty("value2")).isEqualTo("bar");
                });
        }
    }
}
