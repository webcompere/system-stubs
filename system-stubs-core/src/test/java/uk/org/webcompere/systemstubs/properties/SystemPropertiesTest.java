package uk.org.webcompere.systemstubs.properties;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SystemPropertiesTest {

    @Test
    void defaultPropertiesObjectRestoresProperties() throws Exception {
        new SystemProperties()
            .execute(() -> {
               System.setProperty("good", "great");
               assertThat(System.getProperty("good")).isEqualTo("great");
            });

        assertThat(System.getProperty("good")).isNull();
    }

    @Test
    void whenProvidePropertiesBeforeActivationTheyAreAvailableWhenActivated() throws Exception {
        SystemProperties properties = new SystemProperties("a", "b", "c", "d");
        assertThat(System.getProperty("a")).isNull();
        properties.execute(() -> {
            assertThat(System.getProperty("a")).isEqualTo("b");
            assertThat(System.getProperty("c")).isEqualTo("d");
        });
        assertThat(System.getProperty("a")).isNull();
    }

    @Test
    void propertiesSetWhileRunningApplyNextTimeToo() throws Exception{
        SystemProperties properties = new SystemProperties();
        properties.execute(() -> {
            properties.set("g", "h");
            assertThat(System.getProperty("g")).isEqualTo("h");
        });

        assertThat(System.getProperty("g")).isNull();

        properties.execute(() -> {
            assertThat(System.getProperty("g")).isEqualTo("h");
        });
    }
}
