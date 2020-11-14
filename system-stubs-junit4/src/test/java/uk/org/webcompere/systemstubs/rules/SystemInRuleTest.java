package uk.org.webcompere.systemstubs.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import uk.org.webcompere.systemstubs.stream.alt.LinesAltStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(Enclosed.class)
public class SystemInRuleTest {
    public static class SingleTextBlock {
        @Rule
        public SystemInRule systemInRule = new SystemInRule("text" + lineSeparator());

        @Test
        public void canReadText() {
            assertThat(readLinesFromSystemIn(1)).containsExactly("text");
        }

        @Test
        public void canSetException() {
            systemInRule.andExceptionThrownOnInputEnd(new IOException("boom"));
            readLinesFromSystemIn(1);
            assertThatThrownBy(() -> System.in.read()).hasMessage("boom");
        }
    }

    public static class MultiLineBlock {
        @Rule
        public SystemInRule systemInRule = new SystemInRule("line1", "line2");

        @Test
        public void canReadText() {
            assertThat(readLinesFromSystemIn(2)).containsExactly("line1", "line2");
        }
    }

    public static class LineStream {
        @Rule
        public SystemInRule systemInRule = new SystemInRule(new LinesAltStream("line1", "line2"));

        @Test
        public void canReadText() {
            assertThat(readLinesFromSystemIn(2)).containsExactly("line1", "line2");
        }
    }

    public static class AnyStream {
        @Rule
        public SystemInRule systemInRule = new SystemInRule(
            new ByteArrayInputStream("line1\n".getBytes(Charset.defaultCharset())));

        @Test
        public void canReadText() {
            assertThat(readLinesFromSystemIn(1)).containsExactly("line1");
        }
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
