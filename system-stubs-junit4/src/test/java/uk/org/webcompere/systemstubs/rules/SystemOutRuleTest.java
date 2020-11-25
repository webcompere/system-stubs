package uk.org.webcompere.systemstubs.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.systemstubs.stream.output.OutputFactories.tapAndOutput;

@RunWith(Enclosed.class)
public class SystemOutRuleTest {

    public static class WithTappedSystemOut {
        @Rule
        public SystemOutRule systemOutRule = new SystemOutRule();

        @Test
        public void whenWriteToSystemOutItCanBeSeen() {
            // imagine the code under test did this
            System.out.println("I am the system");

            // then the test code can read it
            assertThat(systemOutRule.getLines()).containsExactly("I am the system");
        }
    }

    public static class WithTappedAndStillWritingToConsole {
        @Rule
        public SystemOutRule systemOutRule = new SystemOutRule(tapAndOutput());

        @Test
        public void whenWriteToSystemOutItCanBeSeen() {
            // imagine the code under test did this
            System.out.println("I am the system");

            // then the test code can read it
            assertThat(systemOutRule.getLines()).containsExactly("I am the system");
        }
    }
}
