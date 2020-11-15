package uk.org.webcompere.systemstubs.jupiter.examples;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;
import uk.org.webcompere.systemstubs.stream.SystemOut;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SystemStubsExtension.class)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class MultipleTestResources {
    // protected the environment, allowing us a set method
    @SystemStub
    private EnvironmentVariables environmentVariables;

    // defaults to protecting system properties against change
    @SystemStub
    private SystemProperties systemProperties;

    // defaults to tapping system out
    @SystemStub
    private SystemOut systemOut;

    @Test
    void method1_canDoThingsThatAreIsolatedFromOtherMethods() {
        environmentVariables.set("a", "b");
        systemProperties.set("prop1", "proppy");

        System.out.println("hello world");

        assertThat(System.getenv("a")).isEqualTo("b");
        assertThat(System.getProperty("prop1")).isEqualTo("proppy");

        assertThat(systemOut.getText()).startsWith("hello world");
    }

    @Test
    void method2_isUnaffectedByAllTheEventsOfMethod1() {
        assertThat(System.getenv("a")).isNull();
        assertThat(System.getProperty("prop1")).isNull();

        assertThat(systemOut.getText()).isEmpty();
    }
}
