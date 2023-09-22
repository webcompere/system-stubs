package uk.org.webcompere.systemstubs.testng;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

import static org.assertj.core.api.Assertions.assertThat;

@Listeners(SystemStubsListener.class)
public class SystemStubsPluginWhenStubIsBlankTest {

    @SystemStub
    private EnvironmentVariables environmentVariables;

    @BeforeTest
    public void beforeTest() {
        // even though the stub looks to be null, it's instantiated by here
        environmentVariables.set("setinbefore", "yes");
    }

    @Test
    public void noEnvironmentVariable() {
        assertThat(System.getenv("scooby")).isBlank();
    }

    @Test
    public void hasEnvironmentVariable() {
        environmentVariables.set("foo", "bar");

        assertThat(System.getenv("foo")).isEqualTo("bar");
    }
}
