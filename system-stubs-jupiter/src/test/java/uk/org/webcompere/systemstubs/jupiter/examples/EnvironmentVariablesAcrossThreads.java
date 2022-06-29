package uk.org.webcompere.systemstubs.jupiter.examples;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static java.lang.System.getenv;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SystemStubsExtension.class)
class EnvironmentVariablesAcrossThreads {

    @SystemStub
    private EnvironmentVariables environmentVariables = new EnvironmentVariables("foo", "bar");

    @Test
    void theVariablesAreSetInTheTestMainThread() {
        assertThat(getenv("foo")).isEqualTo("bar");
    }

    @Test
    void theVariablesAreVisibleToWorkerThreads() throws Exception {
        Map<String, String> result = new HashMap<>();

        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            result.put("foo", getenv("foo"));
            latch.countDown();
        }).start();

        latch.await();

        assertThat(result).containsEntry("foo", "bar");
    }
}
