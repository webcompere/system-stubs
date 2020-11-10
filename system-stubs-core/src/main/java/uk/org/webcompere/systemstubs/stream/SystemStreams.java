package uk.org.webcompere.systemstubs.stream;

import uk.org.webcompere.systemstubs.ThrowingRunnable;

import java.io.OutputStream;

public class SystemStreams {

    public static void executeWithSystemErrReplacement(OutputStream replacementForErr,
        ThrowingRunnable throwingRunnable) throws Exception {
        new SystemErr(replacementForErr)
            .execute(throwingRunnable.asCallable());
    }

    public static void executeWithSystemOutReplacement(OutputStream replacementForOut,
        ThrowingRunnable throwingRunnable) throws Exception {
        new SystemOut(replacementForOut)
            .execute(throwingRunnable.asCallable());
    }

}
