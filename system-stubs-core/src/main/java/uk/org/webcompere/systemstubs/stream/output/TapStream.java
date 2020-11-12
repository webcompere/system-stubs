package uk.org.webcompere.systemstubs.stream.output;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * TapStream is a {@link ByteArrayOutputStream} that satisfies the {@link Output}
 * interface too.
 */
public class TapStream extends ByteArrayOutputStream implements Output<TapStream> {
    @Override
    public String getText() {
        return super.toString();
    }

    @Override
    public TapStream getOutputStream() {
        return this;
    }

    @Override
    public void clear() {
        reset();
    }
}
