package uk.org.webcompere.systemstubs.stream;

import uk.org.webcompere.systemstubs.ThrowingRunnable;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static java.lang.System.*;
import static java.nio.charset.Charset.defaultCharset;

public class SystemStreams {

    public static void executeWithSystemErrReplacement(OutputStream replacementForErr,
        ThrowingRunnable throwingRunnable) throws Exception {
        new SystemErr(replacementForErr)
            .executeAround(throwingRunnable.asCallable());
    }

    public static void executeWithSystemOutReplacement(OutputStream replacementForOut,
        ThrowingRunnable throwingRunnable) throws Exception {
        new SystemOut(replacementForOut)
            .executeAround(throwingRunnable.asCallable());
    }

}
