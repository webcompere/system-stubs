package uk.org.webcompere.systemstubs.rules;

import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SystemOutRuleTest {

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
