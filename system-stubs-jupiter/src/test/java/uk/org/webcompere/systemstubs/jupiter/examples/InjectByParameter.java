package uk.org.webcompere.systemstubs.jupiter.examples;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.security.AbortExecutionException;
import uk.org.webcompere.systemstubs.security.SystemExit;
import uk.org.webcompere.systemstubs.stream.SystemErrAndOut;
import uk.org.webcompere.systemstubs.stream.SystemIn;
import uk.org.webcompere.systemstubs.stream.input.LinesAltStream;

import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SystemStubsExtension.class)
public class InjectByParameter {
    @Test
    void useALocalSystemExit(SystemExit exit) {
        assertThatThrownBy(() -> System.exit(123))
            .isInstanceOf(AbortExecutionException.class);

        assertThat(exit.getExitCode()).isEqualTo(123);
    }

    @Test
    void tapErrorAndOutAndProvideSystemIn(SystemErrAndOut errAndOut, SystemIn systemIn) {
        // given the system in contains lines
        systemIn.setInputStream(new LinesAltStream("one", "two", "three"));

        // when the algorithm runs
        try {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println(scanner.nextLine());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        // then the output is both the out and errs
        assertThat(errAndOut.getLines()).containsExactly("one", "two", "three", "No line found");
    }
}
