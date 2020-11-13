package uk.org.webcompere.systemstubs.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import uk.org.webcompere.systemstubs.stream.output.DisallowWriteStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(Enclosed.class)
public class SystemOutAndSystemErrTest {

    public static class DisallowingWrite {
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

    public static class TappingWrite {
        @Rule
        public SystemErrRule tapErr = new SystemErrRule();

        @Rule
        public SystemOutRule tapOut = new SystemOutRule();

        @Test
        public void whenHitSystemOutThenCanRead() {
            System.out.println("boom");
            assertThat(tapOut.getLines()).containsExactly("boom");
        }

        @Test
        public void whenHitSystemErrThenCanRead() {
            System.err.println("boom");
            assertThat(tapErr.getLines()).containsExactly("boom");
        }
    }
}
