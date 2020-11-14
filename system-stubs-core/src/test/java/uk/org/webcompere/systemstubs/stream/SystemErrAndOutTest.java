package uk.org.webcompere.systemstubs.stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SystemErrAndOutTest {

    @Test
    void canTapSystemErrorWithErrAndOut() throws Exception {
        SystemErrAndOut errAndOut = new SystemErrAndOut();
        errAndOut.execute(() -> {
            System.err.print("hello");
        });
        assertThat(errAndOut.getText()).isEqualTo("hello");
    }

    @Test
    void canTapSystemOutWithErrAndOut() throws Exception {
        SystemErrAndOut errAndOut = new SystemErrAndOut();
        errAndOut.execute(() -> {
            System.out.print("hello out");
        });
        assertThat(errAndOut.getText()).isEqualTo("hello out");
    }

    @Test
    void canClearTheOutputWhileStillInUseAndReuse() throws Exception {
        SystemErrAndOut errAndOut = new SystemErrAndOut();
        errAndOut.execute(() -> {
            System.out.print("hello out");
            assertThat(errAndOut.getText()).isEqualTo("hello out");

            errAndOut.clear();

            System.out.print("hello again");
            assertThat(errAndOut.getText()).isEqualTo("hello again");
        });

    }
}
