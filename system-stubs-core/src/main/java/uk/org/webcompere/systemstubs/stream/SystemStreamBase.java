package uk.org.webcompere.systemstubs.stream;

import uk.org.webcompere.systemstubs.resource.SingularTestResource;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static java.nio.charset.Charset.defaultCharset;

public abstract class SystemStreamBase extends SingularTestResource {
    private static final boolean AUTO_FLUSH = true;
    private static final String DEFAULT_ENCODING = defaultCharset().name();

    protected PrintStream originalStream;
    protected PrintStream replacementStream;

    protected SystemStreamBase(OutputStream stream) {
        try {
            replacementStream = wrap(stream);
        } catch (UnsupportedEncodingException e) {
            throw new StreamException("Cannot wrap stream: " + e.getMessage(), e);
        }
    }

    private static PrintStream wrap(OutputStream outputStream) throws UnsupportedEncodingException {
        return new PrintStream(outputStream,
            AUTO_FLUSH,
            DEFAULT_ENCODING);
    }
}
