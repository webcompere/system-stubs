package uk.org.webcompere.systemstubs.stream.output;

import java.io.OutputStream;

public class NoopStream extends OutputStream implements Output<NoopStream> {
    @Override
    public void write(int b) {
    }

    @Override
    public NoopStream getOutputStream() {
        return this;
    }
}
