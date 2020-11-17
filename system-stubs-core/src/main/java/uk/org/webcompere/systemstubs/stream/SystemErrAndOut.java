package uk.org.webcompere.systemstubs.stream;

import uk.org.webcompere.systemstubs.resource.Resources;
import uk.org.webcompere.systemstubs.resource.TestResource;
import uk.org.webcompere.systemstubs.stream.output.Output;
import uk.org.webcompere.systemstubs.stream.output.TapStream;

import java.io.OutputStream;

import static java.util.Arrays.asList;

/**
 * Composite of both {@link SystemErr} and {@link SystemOut} for when directing
 * both of them to the same stream
 * @since 1.0.0
 */
public class SystemErrAndOut implements Output, TestResource {
    private SystemErr systemErr;
    private SystemOut systemOut;

    public SystemErrAndOut() {
        this(new TapStream());
    }

    public SystemErrAndOut(Output output) {
        systemErr = new SystemErr(output);
        systemOut = new SystemOut(output);
    }

    @Override
    public void setup() throws Exception {
        systemErr.setup();
        systemOut.setup();
    }

    @Override
    public void teardown() throws Exception {
        Resources.executeCleanup(asList(systemOut, systemErr));
    }

    @Override
    public String getText() {
        // both share the output, so use Err's
        return systemErr.getText();
    }

    @Override
    public void clear() {
        // only clear one of them as they share the same output
        systemErr.clear();
    }

    @Override
    public OutputStream getOutputStream() {
        return systemErr.getOutputStream();
    }
}
