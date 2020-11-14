package uk.org.webcompere.systemstubs.stream;

import org.junit.jupiter.api.Test;
import uk.org.webcompere.systemstubs.stream.input.LinesAltStream;
import uk.org.webcompere.systemstubs.stream.input.TextAltStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.spy;

class SystemInTest {
    private List<String> linesRead;

    @Test
    void canReadASingleLineOfTextFromSystemInA() throws Exception {
        new SystemIn("Hello world")
            .execute(() -> linesRead = readLinesFromSystemIn(1));

        assertThat(linesRead).containsExactly("Hello world");
    }

    @Test
    void canReadLinesOfTextFromSystemIn() throws Exception {
        new SystemIn("Hello world", "goodbye world")
            .execute(() -> linesRead = readLinesFromSystemIn(1));

        assertThat(linesRead).containsExactly("Hello world");
    }

    @Test
    void canReadTwoLinesOfTextFromSystemIn() throws Exception {
        new SystemIn("Hello world", "goodbye world")
            .execute(() -> linesRead = readLinesFromSystemIn(2));

        assertThat(linesRead).containsExactly("Hello world", "goodbye world");
    }

    @Test
    void willHaveNoExceptionWhenRunsOutOfTextAtLineBreak() throws Exception {
        new SystemIn("Hello world", "goodbye world")
            .andExceptionThrownOnInputEnd(new IOException("Ran out of text"))
            .execute(() -> readLinesFromSystemIn(2));
    }

    @Test
    void willHaveExceptionWhenRunsOutOfTextAfterLineBreak() throws Exception {
        new SystemIn("Hello world", "goodbye world")
            .andExceptionThrownOnInputEnd(new IOException("Ran out of text"))
            .execute(() -> {
                assertThatThrownBy(() -> readLinesFromSystemIn(3))
                    .hasMessage("No line found");
            });
    }

    @Test
    void willAvoidExceptionByNotRunningOutOfText() throws Exception {
        new SystemIn("Hello world", "goodbye world")
            .andExceptionThrownOnInputEnd(new IOException("Ran out of text"))
            .execute(() -> {
                // reading short of the full text results in no exception
                readLinesFromSystemIn(2);
            });
    }

    @Test
    void canReadInfiniteStreamOfTextFromSystemIn() throws Exception {
        new SystemIn(new LinesAltStream(Stream.generate(() -> UUID.randomUUID().toString())))
            .execute(() -> {
                // can read up to an arbitrary limit
                assertThat(readLinesFromSystemIn(120)).hasSize(120);
            });
    }

    @Test
    void canSetInputStreamWhileSystemInIsActive() throws Exception {
        SystemIn in = new SystemIn();
        in.execute(() -> {
            in.setInputStream(new TextAltStream("foo"));
            Scanner scanner = new Scanner(System.in);
            assertThat(scanner.nextLine()).isEqualTo("foo");
        });
    }

    @Test
    void closesStreamOnTeardown() throws Exception {
        InputStream stream = spy(new TextAltStream("foo"));
        new SystemIn(stream)
            .execute(() -> {
                Scanner scanner = new Scanner(System.in);
                assertThat(scanner.nextLine()).isEqualTo("foo");
            });

        then(stream).should().close();
    }

    /**
     * Read a certain number of lines from System.in
     * @param count the count to read
     * @return the list of lines read
     */
    private static List<String> readLinesFromSystemIn(int count) {
        Scanner scanner = new Scanner(System.in);
        return Stream.generate(scanner::nextLine)
            .limit(count)
            .collect(toList());
    }
}
