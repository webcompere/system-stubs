package uk.org.webcompere.systemstubs;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.org.webcompere.systemstubs.stream.SystemErrAndOut;
import uk.org.webcompere.systemstubs.stream.output.TapStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static uk.org.webcompere.systemstubs.SystemStubs.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class TapSystemErrAndOutTest {

    @Test
    void application_writes_text_to_System_err_and_out() throws Exception {
        String text = tapSystemErrAndOut(() -> {
            System.err.print("text from err");
            System.out.print("text from out");
        });
        assertThat(text).isEqualTo("text from errtext from out");
    }

    @Test
    void construct_system_err_and_out_tap() throws Exception {
        SystemErrAndOut stream = withSystemErrAndOut(new TapStream());
        stream.execute(() -> {
            System.err.println("text from err");
            System.out.println("text from out");
        });
        assertThat(stream.getLines())
            .containsExactly("text from err","text from out");
    }

    @Test
    void construct_system_err_and_out_default_tap() throws Exception {
        SystemErrAndOut stream = withTapSystemErrAndOut();
        stream.execute(() -> {
            System.err.println("text from err");
            System.out.println("text from out");
        });
        assertThat(stream.getLines())
            .containsExactly("text from err","text from out");
    }

    @Nested
    class nothing_written_to_system_err_or_out {
        @Test
        void when_writes_to_out_is_error() {
            assertThatThrownBy(() -> {
                assertNothingWrittenToSystemErrOrOut(() -> {
                    System.out.print("oops");
                });
            }).isInstanceOf(AssertionError.class);

        }

        @Test
        void when_writes_to_err_is_error() {
            assertThatThrownBy(() -> {
                assertNothingWrittenToSystemErrOrOut(() -> {
                    System.err.print("oops");
                });
            }).isInstanceOf(AssertionError.class);
        }

        @Test
        void when_does_nothing_is_ok() throws Exception {
            assertNothingWrittenToSystemErrOrOut(() -> {

            });
        }
    }
}
