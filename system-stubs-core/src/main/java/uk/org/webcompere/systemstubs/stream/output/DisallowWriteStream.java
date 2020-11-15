package uk.org.webcompere.systemstubs.stream.output;

import java.io.OutputStream;

public class DisallowWriteStream extends OutputStream implements Output<DisallowWriteStream> {
    @Override
    public void write(int b) {
        throw new AssertionError("Tried to write '" + (char) b +
            "' although this is not allowed.");
    }

    @Override
    public DisallowWriteStream getOutputStream() {
        return this;
    }
}
