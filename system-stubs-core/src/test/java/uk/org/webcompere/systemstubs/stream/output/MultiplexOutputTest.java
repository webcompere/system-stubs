package uk.org.webcompere.systemstubs.stream.output;

import org.junit.jupiter.api.Test;

import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.systemstubs.stream.SystemStreamBase.wrap;

class MultiplexOutputTest {
    @Test
    void multiplexOneStream_thenCanWrite() throws Exception {
        TapStream tapStream = new TapStream();
        MultiplexOutput multiplexOutput = new MultiplexOutput(tapStream);

        PrintStream printer = wrap(multiplexOutput);
        printer.println("Foo");

        assertThat(multiplexOutput.getLines()).containsExactly("Foo");
        assertThat(tapStream.getLines()).containsExactly("Foo");
    }

    @Test
    void multiplexTwoStream_thenCanWriteToBoth() throws Exception {
        TapStream tapStream1 = new TapStream();
        TapStream tapStream2 = new TapStream();
        MultiplexOutput multiplexOutput = new MultiplexOutput(tapStream1, tapStream2);

        PrintStream printer = wrap(multiplexOutput);
        printer.println("Foo");

        assertThat(multiplexOutput.getLines()).containsExactly("Foo");
        assertThat(tapStream1.getLines()).containsExactly("Foo");
        assertThat(tapStream2.getLines()).containsExactly("Foo");
    }

    @Test
    void multiplexTwoStream_thenCanClearBoth() throws Exception {
        TapStream tapStream1 = new TapStream();
        TapStream tapStream2 = new TapStream();
        MultiplexOutput multiplexOutput = new MultiplexOutput(tapStream1, tapStream2);

        PrintStream printer = wrap(multiplexOutput);
        printer.println("Foo");

        multiplexOutput.clear();

        assertThat(tapStream1.getText()).isEmpty();
        assertThat(tapStream2.getText()).isEmpty();
    }
}
