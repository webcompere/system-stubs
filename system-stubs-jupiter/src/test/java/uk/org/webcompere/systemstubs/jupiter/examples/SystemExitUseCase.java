package uk.org.webcompere.systemstubs.jupiter.examples;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.security.AbortExecutionException;
import uk.org.webcompere.systemstubs.security.SystemExit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SystemStubsExtension.class)
class SystemExitUseCase {
    // the presence of this in the test means System.exit becomes an exception
    @SystemStub
    private SystemExit systemExit;

    @Test
    void doSomethingThatAccidentallyCallsSystemExit() {
        // this test would have stopped the JVM, now it ends in `AbortExecutionException`
        // System.exit(1);
    }

    @Test
    void canCatchSystemExit() {
        assertThatThrownBy(() -> System.exit(1))
            .isInstanceOf(AbortExecutionException.class);

        assertThat(systemExit.getExitCode()).isEqualTo(1);
    }
}
