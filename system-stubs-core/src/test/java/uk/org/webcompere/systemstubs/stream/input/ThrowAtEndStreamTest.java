package uk.org.webcompere.systemstubs.stream.input;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ThrowAtEndStreamTest {
    private AltInputStream wrapped = new LinesAltStream("first", "second");
    private AltInputStream emptyWrapped = new LinesAltStream();

    @Test
    void wrappedStreamYieldsLines() {
        ThrowAtEndStream throwAtEndStream = new ThrowAtEndStream(wrapped, new IOException("boom"));
        Scanner scanner = new Scanner(throwAtEndStream);
        assertThat(scanner.nextLine()).isEqualTo("first");
        assertThat(scanner.nextLine()).isEqualTo("second");

        // scanner has its own exception but the stream throws the canned exception
        assertThatThrownBy(scanner::nextLine).hasMessage("No line found");
        assertThatThrownBy(throwAtEndStream::read).hasMessage("boom");
    }

    @Test
    void emptyStreamYieldsNoLines() {
        ThrowAtEndStream throwAtEndStream = new ThrowAtEndStream(emptyWrapped, new IOException("boom"));
        Scanner scanner = new Scanner(throwAtEndStream);

        assertThatThrownBy(scanner::nextLine).hasMessage("No line found");
    }

    @Test
    void streamCannotReadWithNullBuffer() {
        ThrowAtEndStream stream = new ThrowAtEndStream(wrapped, new IOException());
        assertThatThrownBy(() -> stream.read(null, 0, 128))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void streamCannotReadWithBufferTooShort() {
        ThrowAtEndStream stream = new ThrowAtEndStream(wrapped, new IOException());
        assertThatThrownBy(() -> stream.read(new byte[12], 0, 128))
            .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void returnsNothingWhenLengthIsZero() throws Exception {
        ThrowAtEndStream stream = new ThrowAtEndStream(wrapped, new IOException());
        assertThat(stream.read(new byte[12], 0, 0)).isZero();
    }

}
