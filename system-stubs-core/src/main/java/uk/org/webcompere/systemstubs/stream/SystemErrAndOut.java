package uk.org.webcompere.systemstubs.stream;

import uk.org.webcompere.systemstubs.resource.Resources;
import uk.org.webcompere.systemstubs.resource.TestResource;
import uk.org.webcompere.systemstubs.stream.output.Output;
import uk.org.webcompere.systemstubs.stream.output.OutputFactory;
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

    /**
     * Default constructor uses {@link TapStream}, shared for both <code>System.out</code> and <code>System.err</code>
     */
    public SystemErrAndOut() {
        this(new TapStream());
    }

    /**
     * Construct with an output shared from the an output created against the <code>System.out</code> original
     * @param outputFactory the output factory to create the output
     */
    public SystemErrAndOut(OutputFactory<? extends OutputStream> outputFactory) {
        systemOut = new SystemOut(outputFactory);
        systemErr = new SystemErr(outputStream -> systemOut.getOutput());
    }

    /**
     * Construct with a target to direct both System out and error to
     * @param output the output target
     */
    public SystemErrAndOut(Output<? extends OutputStream> output) {
        this(output.factoryOfSelf());
    }

    @Override
    public void setup() throws Exception {
        systemOut.setup();
        systemErr.setup();
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
