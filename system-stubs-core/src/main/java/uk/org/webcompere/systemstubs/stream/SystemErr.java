package uk.org.webcompere.systemstubs.stream;

import uk.org.webcompere.systemstubs.resource.TestResource;

import java.io.OutputStream;

import static java.lang.System.err;
import static java.lang.System.setErr;

/**
 * Replace System.err with an alternative
 */
public class SystemErr extends SystemStreamBase {

    /**
     * Construct with the {@link OutputStream} to use in place of <code>System.err</code>
     * @param stream new output stream
     */
    public SystemErr(OutputStream stream) {
        super(stream);
    }

    @Override
    protected void doSetup() throws Exception {
        originalStream = err;
        setErr(replacementStream);
    }

    @Override
    protected void doTeardown() throws Exception {
        setErr(originalStream);
    }
}
