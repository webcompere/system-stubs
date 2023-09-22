package uk.org.webcompere.systemstubs.testng.examples;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.testng.SystemStub;
import uk.org.webcompere.systemstubs.testng.SystemStubsListener;

import static org.assertj.core.api.Assertions.assertThat;

public class SystemStubsWithoutPluginTest {

    private EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @BeforeTest
    public void beforeTest() throws Exception {
        environmentVariables.set("setinbefore", "yes");

        environmentVariables.setup();
    }

    @AfterTest
    public void afterTest() throws Exception {
        environmentVariables.teardown();
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

    @Test
    public void environmentSetInBeforeWillApplyInTest() {
        assertThat(System.getenv("setinbefore")).isEqualTo("yes");
    }
}
