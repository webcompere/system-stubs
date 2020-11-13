package uk.org.webcompere.systemstubs.stream;

import org.junit.jupiter.api.Test;
import uk.org.webcompere.systemstubs.stream.output.NoopStream;

import static org.assertj.core.api.Assertions.assertThat;

class SystemStreamBaseTest {
    private SystemOut systemOut = new SystemOut();
    private SystemOut noopOut = new SystemOut(new NoopStream());

    @Test
    void canReadText() throws Exception {
        systemOut.execute(() -> System.out.println("Hello"));

        assertThat(systemOut.getText()).startsWith("Hello");
    }

    @Test
    void canReadLines() throws Exception {
        systemOut.execute(() -> {
            System.out.println("foo");
            System.out.println("bar");
        });

        assertThat(systemOut.getLines()).containsExactly("foo", "bar");
    }

    @Test
    void canReuseSystemOutputAsItClearsInBetween() throws Exception {
        systemOut.execute(() -> System.out.println("Hello"));

        systemOut.execute(() -> System.out.println("World"));

        assertThat(systemOut.getLinesNormalized())
            .isEqualTo("World\n");
    }

    @Test
    void canClearSystemOut() throws Exception {
        systemOut.execute(() -> System.out.println("Hello"));

        systemOut.clear();

        assertThat(systemOut.getText())
            .isEmpty();
    }

    @Test
    void noOpStreamHasNoText() throws Exception {
        noopOut.execute(() -> System.out.println("Anyone home?"));

        assertThat(noopOut.getText()).isEmpty();
    }
}
