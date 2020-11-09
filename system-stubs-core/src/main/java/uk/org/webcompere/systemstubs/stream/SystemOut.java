package uk.org.webcompere.systemstubs.stream;

import java.io.OutputStream;

import static java.lang.System.*;

/**
 * Replace System.out with an alternative
 */
public class SystemOut extends SystemStreamBase {
    /**
     * Construct with the {@link OutputStream} to use in place of <code>System.out</code>
     * @param stream new output stream
     */
    public SystemOut(OutputStream stream) {
        super(stream);
    }

    @Override
    protected void doSetup() throws Exception {
        originalStream = out;
        setOut(replacementStream);
    }

    @Override
    protected void doTeardown() throws Exception {
        setOut(originalStream);
    }
}
