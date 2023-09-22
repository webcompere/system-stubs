package uk.org.webcompere.systemstubs.testng.examples;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import uk.org.webcompere.systemstubs.stream.SystemOut;
import uk.org.webcompere.systemstubs.testng.SystemStub;
import uk.org.webcompere.systemstubs.testng.SystemStubsListener;

import static org.assertj.core.api.Assertions.assertThat;

@Listeners(SystemStubsListener.class)
public class CaptureSystemOutTest {

    @SystemStub
    private SystemOut out;

    @BeforeTest
    public void beforeTest() {
        out.clear();
    }

    @Test
    public void canReadThingsSentToSystemOut() {
        // simulate the system under test writing to std out
        System.out.println("Can I assert this?");

        assertThat(out.getText()).startsWith("Can I assert this?");
    }
}
