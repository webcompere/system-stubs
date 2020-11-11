package uk.org.webcompere.systemstubs.rules;

import org.junit.Rule;
import org.junit.Test;
import uk.org.webcompere.systemstubs.security.AbortExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SystemExitRuleTest {
    @Rule
    public SystemExitRule systemExitRule = new SystemExitRule();

    @Test
    public void theCodeExitsButTheProgramDoesnt() {
        assertThatThrownBy(() -> {
            // some test code performs an exit
            System.exit(92);
        }).isInstanceOf(AbortExecutionException.class);

        assertThat(systemExitRule.getExitCode()).isEqualTo(92);
    }

    @Test
    public void noSystemExit() {
        assertThat(systemExitRule.getExitCode()).isNull();
    }
}
