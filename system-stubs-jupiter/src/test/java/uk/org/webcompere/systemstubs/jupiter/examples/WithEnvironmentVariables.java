package uk.org.webcompere.systemstubs.jupiter.examples;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SystemStubsExtension.class)
class WithEnvironmentVariables {

    @SystemStub
    private EnvironmentVariables variables = new EnvironmentVariables("input", "foo");

    @Test
    void hasAccessToEnvironmentVariables() {
        assertThat(System.getenv("input")).isEqualTo("foo");
    }

    @Test
    void changeEnvironmentVariablesDuringTest() {
        variables.set("input", "bar");

        assertThat(System.getenv("input"))
            .isEqualTo("bar");
    }
}
