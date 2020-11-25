package uk.org.webcompere.systemstubs.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import uk.org.webcompere.systemstubs.stream.output.TapStream;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.systemstubs.stream.output.OutputFactories.tapAndOutput;

@RunWith(Enclosed.class)
public class SystemErrAndOutRuleTest {

    public static class ExplicitOutput {
        @Rule
        public SystemErrAndOutRule errAndOutRule = new SystemErrAndOutRule(new TapStream());

        @Test
        public void whenNothingIsWrittenNoTextIsFound() {
            assertThat(errAndOutRule.getText()).isEmpty();
        }

        @Test
        public void whenWriteToOutThenItCanBeFound() {
            System.out.println("Some text");
            assertThat(errAndOutRule.getLines()).contains("Some text");
        }

        @Test
        public void whenWriteToErrThenItCanBeFound() {
            System.err.println("Some text");
            assertThat(errAndOutRule.getLines()).contains("Some text");
        }
    }

    public static class DefaultOutput {
        @Rule
        public SystemErrAndOutRule errAndOutRule = new SystemErrAndOutRule();

        @Test
        public void whenNothingIsWrittenNoTextIsFound() {
            assertThat(errAndOutRule.getText()).isEmpty();
        }

        @Test
        public void whenWriteToOutThenItCanBeFound() {
            System.out.println("Some text");
            assertThat(errAndOutRule.getLines()).contains("Some text");
        }

        @Test
        public void whenWriteToErrThenItCanBeFound() {
            System.err.println("Some text");
            assertThat(errAndOutRule.getLines()).contains("Some text");
        }
    }

    public static class TapWhileAlsoWritingToConsole {
        @Rule
        public SystemErrAndOutRule errAndOutRule = new SystemErrAndOutRule(tapAndOutput());

        @Test
        public void whenWriteToOutThenItCanBeFound() {
            System.out.println("Some text");
            assertThat(errAndOutRule.getLines()).contains("Some text");
        }

        @Test
        public void whenWriteToErrThenItCanBeFound() {
            System.err.println("Some text");
            assertThat(errAndOutRule.getLines()).contains("Some text");
        }
    }
}
