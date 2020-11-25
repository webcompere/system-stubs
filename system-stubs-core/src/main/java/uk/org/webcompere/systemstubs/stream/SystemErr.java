package uk.org.webcompere.systemstubs.stream;

import uk.org.webcompere.systemstubs.stream.output.Output;
import uk.org.webcompere.systemstubs.stream.output.OutputFactory;

import java.io.OutputStream;

/**
 * Replace System.err with an alternative
 * @since 1.0.0
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
     * Construct with a {@link OutputFactory} for creating output objects from the existing output
     * @param outputFactory the factory to use
     */
    public SystemErr(OutputFactory<? extends OutputStream> outputFactory) {
        super(outputFactory, System::setErr, () -> System.err);
    }

    /**
     * Default constructor, taps the output
     */
    public SystemErr() {
        super(System::setErr, () -> System.err);
    }
}
