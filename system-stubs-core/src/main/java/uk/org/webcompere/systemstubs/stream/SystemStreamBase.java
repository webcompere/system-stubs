package uk.org.webcompere.systemstubs.stream;

import uk.org.webcompere.systemstubs.resource.SingularTestResource;
import uk.org.webcompere.systemstubs.stream.output.Output;
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
 */
public class SystemStreamBase extends SingularTestResource implements Output<OutputStream> {
    private static final boolean AUTO_FLUSH = true;
    private static final String DEFAULT_ENCODING = defaultCharset().name();

    protected PrintStream originalStream;
    protected Output<? extends OutputStream> target;
    protected Consumer<PrintStream> printStreamSetter;
    protected Supplier<PrintStream> printStreamGetter;

    protected <T extends OutputStream> SystemStreamBase(Consumer<PrintStream> printStreamSetter,
                                                        Supplier<PrintStream> printStreamGetter) {
        this(new TapStream(), printStreamSetter, printStreamGetter);
    }

    protected <T extends OutputStream> SystemStreamBase(Output<? extends OutputStream> target,
                                                        Consumer<PrintStream> printStreamSetter,
                                                        Supplier<PrintStream> printStreamGetter) {
        this.target = target;
        this.printStreamSetter = printStreamSetter;
        this.printStreamGetter = printStreamGetter;
    }

    private static PrintStream wrap(OutputStream outputStream) throws UnsupportedEncodingException {
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
            printStreamSetter.accept(wrap(target.getOutputStream()));
        } catch (UnsupportedEncodingException e) {
        throw new StreamException("Cannot wrap stream: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doTeardown() throws Exception {
        printStreamSetter.accept(originalStream);
    }

    @Override
    public String getText() {
        return target.getText();
    }

    @Override
    public void clear() {
        target.clear();
    }

    @Override
    public OutputStream getOutputStream() {
        return target.getOutputStream();
    }
}
