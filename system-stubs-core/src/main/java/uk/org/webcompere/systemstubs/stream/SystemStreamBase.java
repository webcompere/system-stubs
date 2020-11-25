package uk.org.webcompere.systemstubs.stream;

import uk.org.webcompere.systemstubs.resource.SingularTestResource;
import uk.org.webcompere.systemstubs.stream.output.Output;
import uk.org.webcompere.systemstubs.stream.output.OutputFactory;
import uk.org.webcompere.systemstubs.stream.output.TapStream;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.nio.charset.Charset.defaultCharset;

/**
 * A general purpose replacement for a system stream. Uses an {@link Output} object
 * in place of the intended stream while active. Presents methods to access the output.
 * When active converts the output into a {@link PrintStream} and applies it to the
 * environment.
 * @since 1.0.0
 */
public class SystemStreamBase extends SingularTestResource implements Output<OutputStream> {
    private static final boolean AUTO_FLUSH = true;
    private static final String DEFAULT_ENCODING = defaultCharset().name();

    protected PrintStream originalStream;
    protected Output<? extends OutputStream> currentTarget;
    protected OutputFactory<? extends OutputStream> targetFactory;
    protected Consumer<PrintStream> printStreamSetter;
    protected Supplier<PrintStream> printStreamGetter;

    protected <T extends OutputStream> SystemStreamBase(Consumer<PrintStream> printStreamSetter,
                                                        Supplier<PrintStream> printStreamGetter) {
        this(new TapStream(), printStreamSetter, printStreamGetter);
    }

    protected <T extends OutputStream> SystemStreamBase(Output<T> target,
                                                        Consumer<PrintStream> printStreamSetter,
                                                        Supplier<PrintStream> printStreamGetter) {
        this(target.factoryOfSelf(), printStreamSetter, printStreamGetter);
    }

    protected <T extends OutputStream> SystemStreamBase(OutputFactory<T> targetFactory,
                                                        Consumer<PrintStream> printStreamSetter,
                                                        Supplier<PrintStream> printStreamGetter) {
        this.targetFactory = targetFactory;
        this.printStreamSetter = printStreamSetter;
        this.printStreamGetter = printStreamGetter;
    }

    /**
     * Convert an output stream to a {@link PrintStream}
     * @param outputStream the output stream to use
     * @return a {@link PrintStream} that can be written to
     * @throws UnsupportedEncodingException on errors constructing the stream (unlikely)
     */
    public static PrintStream wrap(OutputStream outputStream) throws UnsupportedEncodingException {
        return new PrintStream(outputStream,
            AUTO_FLUSH,
            DEFAULT_ENCODING);
    }

    @Override
    protected void doSetup() throws Exception {
        // in case this is being reused, it is cleared on setup
        clear();

        originalStream = printStreamGetter.get();
        try {
            currentTarget = targetFactory.apply(originalStream);
            printStreamSetter.accept(wrap(currentTarget.getOutputStream()));
        } catch (UnsupportedEncodingException e) {
            throw new StreamException("Cannot wrap stream: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doTeardown() throws Exception {
        printStreamSetter.accept(originalStream);

        // for outputs like files, that need to be closed
        currentTarget.closeOutput();
    }

    @Override
    public String getText() {
        return currentTarget.getText();
    }

    @Override
    public void clear() {
        if (currentTarget != null) {
            currentTarget.clear();
        }
    }

    @Override
    public OutputStream getOutputStream() {
        if (currentTarget == null) {
            return null;
        }
        return currentTarget.getOutputStream();
    }
}
