package uk.org.webcompere.systemstubs.stream.output;

import java.io.OutputStream;

/**
 * An {@link Output} that discards anything written to it. It can be used to mute
 * <code>System.err</code> for example.
 */
public class NoopStream extends OutputStream implements Output<NoopStream> {
    @Override
    public void write(int b) {
    }

    @Override
    public NoopStream getOutputStream() {
        return this;
    }
}
