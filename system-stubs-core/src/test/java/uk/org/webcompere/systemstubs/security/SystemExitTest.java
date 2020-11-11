package uk.org.webcompere.systemstubs.security;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SystemExitTest {

    private SystemExit systemExit = new SystemExit();

    @Test
    void whenNoExitThenNoExitCode() {
        assertThat(systemExit.getExitCode()).isNull();
    }

    @Test
    void whenOneExitThenExitCodeProvided() throws Exception {
        systemExit.execute(() -> {
            System.exit(123);
        });

        // can also be seen outside of "execute"
        assertThat(systemExit.getExitCode()).isEqualTo(123);
    }

    @Test
    void whenTwoExitsThenSecondDoesntApplyAndCodeTerminates() throws Exception {
        List<String> list = new ArrayList<>();
        systemExit.execute(() -> {
            System.exit(123);
            System.exit(234);
            list.add("a");
        });

        assertThat(systemExit.getExitCode()).isEqualTo(123);
        assertThat(list.isEmpty());
    }

    @Test
    void whenTwoActivationsThenEachIsUnique() throws Exception {
        systemExit.execute(() -> {
            System.exit(123);
            assertThat(systemExit.getExitCode()).isEqualTo(123);
        });

        assertThat(systemExit.getExitCode()).isEqualTo(123);

        systemExit.execute(() -> {
            System.exit(234);
            assertThat(systemExit.getExitCode()).isEqualTo(234);
        });

        assertThat(systemExit.getExitCode()).isEqualTo(234);
    }

    @Test
    void localSystemExitObjectExampleForIgnore() throws Exception {
        new SystemExit()
            .execute(() -> {
                System.exit(0);
            });
    }

    @Test
    void localSystemExitObjectExampleForCapture() throws Exception {
        SystemExit exit = new SystemExit();
        exit.execute(() -> {
                System.exit(0);
            });

        assertThat(exit.getExitCode()).isEqualTo(0);
    }
}
