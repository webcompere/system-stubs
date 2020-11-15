package uk.org.webcompere.systemstubs.stream.output;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

/**
 * TapStream is a {@link ByteArrayOutputStream} that satisfies the {@link Output}
 * interface too.
 */
public class TapStream extends ByteArrayOutputStream implements Output<TapStream> {
    @Override
    public String getText() {
        return new String(getOutputStream().toByteArray(), Charset.defaultCharset());
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
