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

    @Test
    void canRunPropertiesNested() throws Exception {
        assertThat(System.getProperty("bar")).isNull();

        SystemProperties properties = new SystemProperties();
        properties.execute(() -> {
            properties.set("bar", "h");
            assertThat(System.getProperty("bar")).isEqualTo("h");


            SystemProperties nested = new SystemProperties();
            nested.execute(() -> {
                properties.set("bar", "zh");
                assertThat(System.getProperty("bar")).isEqualTo("zh");
            });

            assertThat(System.getProperty("bar")).isEqualTo("h");
        });
    }

    @Test
    void canDeletePropertiesNested() throws Exception {
        assertThat(System.getProperty("bar")).isNull();

        SystemProperties properties = new SystemProperties();
        properties.execute(() -> {
            properties.set("bar", "h");
            assertThat(System.getProperty("bar")).isEqualTo("h");


            SystemProperties nested = new SystemProperties();
            nested.execute(() -> {
                System.getProperties().remove("bar");
                assertThat(System.getProperty("bar")).isNull();
            });

            assertThat(System.getProperty("bar")).isEqualTo("h");
        });
    }

    @Test
    void canDeletePropertiesNestedViaPropertiesObject() throws Exception {
        assertThat(System.getProperty("bar")).isNull();

        SystemProperties properties = new SystemProperties();
        properties.execute(() -> {
            properties.set("bar", "h");
            assertThat(System.getProperty("bar")).isEqualTo("h");


            SystemProperties nested = new SystemProperties();
            nested.execute(() -> {
                nested.remove("bar");
                assertThat(System.getProperty("bar")).isNull();
            });

            assertThat(System.getProperty("bar")).isEqualTo("h");
        });
    }

    @Test
    void canPreDeletePropertiesNestedViaPropertiesObject() throws Exception {
        assertThat(System.getProperty("bar")).isNull();

        SystemProperties properties = new SystemProperties();
        properties.execute(() -> {
            properties.set("bar", "h");
            assertThat(System.getProperty("bar")).isEqualTo("h");

            SystemProperties nested = new SystemProperties();
            nested.remove("bar");
            nested.execute(() -> {
                assertThat(System.getProperty("bar")).isNull();
            });
        });
    }

    @Test
    void canPreDeleteMultiplePropertiesNestedViaPropertiesObject() throws Exception {
        SystemProperties properties = new SystemProperties();
        properties.execute(() -> {
            properties.set("bar", "h");
            properties.set("baz", "h");
            assertThat(System.getProperty("bar")).isEqualTo("h");
            assertThat(System.getProperty("baz")).isEqualTo("h");

            SystemProperties nested = new SystemProperties();
            nested.remove("bar").remove("baz");
            nested.execute(() -> {
                assertThat(System.getProperty("bar")).isNull();
                assertThat(System.getProperty("baz")).isNull();
            });
        });
    }

    @Test
    void settingAfterPreDeleteAlsoWorks() throws Exception {
        assertThat(System.getProperty("bar")).isNull();

        SystemProperties properties = new SystemProperties();
        properties.execute(() -> {
            properties.set("bar", "h");
            assertThat(System.getProperty("bar")).isEqualTo("h");

            SystemProperties nested = new SystemProperties().remove("bar");
            nested.execute(() -> {
                nested.set("bar", "bong");
                assertThat(System.getProperty("bar")).isEqualTo("bong");
            });
        });
    }
}
