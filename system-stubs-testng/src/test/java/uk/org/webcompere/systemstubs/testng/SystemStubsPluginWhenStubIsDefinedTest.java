package uk.org.webcompere.systemstubs.testng;

import org.testng.IInvokedMethod;
import org.testng.annotations.*;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

import static org.assertj.core.api.Assertions.assertThat;

@Listeners(SystemStubsListener.class)
public class SystemStubsPluginWhenStubIsDefinedTest {

    @SystemStub
    private EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @BeforeTest
    public void beforeTest() {
        environmentVariables.set("setinbefore", "yes");

        // shouldn't apply yet, as we're not inside a test
        assertThat(System.getenv("setinbefore")).isNotEqualTo("yes");
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
