package uk.org.webcompere.systemstubs.rules;

import org.junit.Rule;
import org.junit.Test;
import uk.org.webcompere.systemstubs.stream.output.DisallowWriteStream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SystemOutAndErrTest {

    @Rule
    public SystemErrRule noErrors = new SystemErrRule(new DisallowWriteStream());

    @Rule
    public SystemOutRule noOutput = new SystemOutRule(new DisallowWriteStream());

    @Test
    public void codeThatDoesNotOutputIsOk() {
        // this code doesn't hit system.out
    }

    @Test
    public void whenHitSystemOutThenIsError() {
        assertThatThrownBy(() -> System.out.println("boom"))
            .isInstanceOf(AssertionError.class);
    }
    @Test
    public void whenHitSystemErrThenIsError() {
        assertThatThrownBy(() -> System.err.println("boom"))
            .isInstanceOf(AssertionError.class);
    }
}
