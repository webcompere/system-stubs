package uk.org.webcompere.systemstubs.stream.input;

import java.io.IOException;
import java.io.InputStream;

/**
 * Decorates one stream by delegating to another
 */
public class DecoratingAltStream extends AltInputStream {
    private InputStream decoratee;

    /**
     * Construct the stream with the decoratee
     * @param decoratee the input stream that will be read from
     */
    public DecoratingAltStream(InputStream decoratee) {
        this.decoratee = decoratee;
    }

    @Override
    public int read() throws IOException {
        return decoratee.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return decoratee.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        decoratee.close();
    }

    @Override
    public boolean contains(Class<? extends InputStream> stream) {
        return super.contains(stream) ||
            stream.isAssignableFrom(decoratee.getClass()) ||
            (decoratee instanceof AltInputStream && ((AltInputStream)decoratee).contains(stream));
    }
}
