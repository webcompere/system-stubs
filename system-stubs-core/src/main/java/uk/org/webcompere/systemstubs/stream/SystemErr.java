package uk.org.webcompere.systemstubs.stream;

import uk.org.webcompere.systemstubs.stream.output.Output;

import java.io.OutputStream;

/**
 * Replace System.err with an alternative
 */
public class SystemErr extends SystemStreamBase {

    /**
     * Construct with the {@link OutputStream} to use in place of <code>System.err</code>
     * @param output new output target
     */
    public SystemErr(Output<? extends OutputStream> output) {
        super(output, System::setErr, () -> System.err);
    }

    /**
     * Default constructor, taps the output
     */
    public SystemErr() {
        super(System::setErr, () -> System.err);
    }
}
