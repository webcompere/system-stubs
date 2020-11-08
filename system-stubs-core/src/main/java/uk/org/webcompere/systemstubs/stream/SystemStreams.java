package uk.org.webcompere.systemstubs.stream;

import uk.org.webcompere.systemstubs.ThrowingRunnable;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static java.lang.System.*;
import static java.nio.charset.Charset.defaultCharset;

public class SystemStreams {

    private static final boolean AUTO_FLUSH = true;
    private static final String DEFAULT_ENCODING = defaultCharset().name();

    public static void executeWithSystemErrReplacement(
        OutputStream replacementForErr,
        ThrowingRunnable throwingRunnable
    ) throws Exception {
        PrintStream originalStream = err;
        try {
            setErr(wrap(replacementForErr));
            throwingRunnable.run();
        } finally {
            setErr(originalStream);
        }
    }

    public static void executeWithSystemOutReplacement(
        OutputStream replacementForOut,
        ThrowingRunnable throwingRunnable
    ) throws Exception {
        PrintStream originalStream = out;
        try {
            setOut(wrap(replacementForOut));
            throwingRunnable.run();
        } finally {
            setOut(originalStream);
        }
    }

    private static PrintStream wrap(
        OutputStream outputStream
    ) throws UnsupportedEncodingException {
        return new PrintStream(
            outputStream,
            AUTO_FLUSH,
            DEFAULT_ENCODING
        );
    }
}
