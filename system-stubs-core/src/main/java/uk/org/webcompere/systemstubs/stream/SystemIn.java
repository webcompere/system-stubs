package uk.org.webcompere.systemstubs.stream;

import uk.org.webcompere.systemstubs.ThrowingRunnable;
import uk.org.webcompere.systemstubs.resource.Resources;
import uk.org.webcompere.systemstubs.resource.SingularTestResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import static java.lang.System.getProperty;
import static java.lang.System.setIn;
import static java.nio.charset.Charset.defaultCharset;

/**
 * A stub that defines the text provided by {@code System.in}. The methods
 * {@link #andExceptionThrownOnInputEnd(IOException)} and
 * {@link #andExceptionThrownOnInputEnd(RuntimeException)} can be used to
 * simulate a {@code System.in} that throws an exception.
 * <p>The specified behaviour of {@code System.in} is applied to an
 * arbitrary piece of code that is provided to {@link #execute(ThrowingRunnable)}.
 */
public class SystemIn extends SingularTestResource {
    private IOException ioException;
    private RuntimeException runtimeException;
    private final String text;
    private InputStream originalIn;

    public SystemIn(String text) {
        this.text = text;
    }

    /**
     * Sets an exception that is thrown after the text is read.
     * @param exception the {@code IOException} to be thrown.
     * @return the {@code SystemInStub} itself.
     * @throws IllegalStateException if a {@code RuntimeException} was
     * already set by
     * {@link #andExceptionThrownOnInputEnd(RuntimeException)}
     */
    public SystemIn andExceptionThrownOnInputEnd(IOException exception) {
        if (runtimeException != null) {
            throw new IllegalStateException("You cannot call"
                + " andExceptionThrownOnInputEnd(IOException) because"
                + " andExceptionThrownOnInputEnd(RuntimeException) has"
                + " already been called.");
        }
        this.ioException = exception;
        return this;
    }

    /**
     * Sets an exception that is thrown after the text is read.
     * @param exception the {@code RuntimeException} to be thrown.
     * @return the {@code SystemInStub} itself.
     * @throws IllegalStateException if an {@code IOException} was already
     * set by {@link #andExceptionThrownOnInputEnd(IOException)}
     */
    public SystemIn andExceptionThrownOnInputEnd(RuntimeException exception) {
        if (ioException != null)
            throw new IllegalStateException("You cannot call"
                + " andExceptionThrownOnInputEnd(RuntimeException) because"
                + " andExceptionThrownOnInputEnd(IOException) has already"
                + " been called.");
        this.runtimeException = exception;
        return this;
    }

    /**
     * Executes the statement and lets {@code System.in} provide the
     * specified text during the execution. After the text was read it
     * throws and exception when {@code System.in#read} is called and an
     * exception was specified by
     * {@link #andExceptionThrownOnInputEnd(IOException)} or
     * {@link #andExceptionThrownOnInputEnd(RuntimeException)}.
     * @param throwingRunnable an arbitrary piece of code.
     * @throws Exception any exception thrown by the statement.
     */
    public void execute(ThrowingRunnable throwingRunnable) throws Exception {
        Resources.execute(throwingRunnable.asCallable(), this);
    }

    @Override
    protected void doSetup() throws Exception {
        InputStream stubStream = new ReplacementInputStream(text, ioException, runtimeException);
        originalIn = System.in;
        setIn(stubStream);
    }

    @Override
    protected void doTeardown() throws Exception {
        setIn(originalIn);
    }

    private static class ReplacementInputStream extends InputStream {
        private final StringReader reader;
        private final IOException ioException;
        private final RuntimeException runtimeException;

        ReplacementInputStream(String text,
            IOException ioException,
            RuntimeException runtimeException) {
            this.reader = new StringReader(text);
            this.ioException = ioException;
            this.runtimeException = runtimeException;
        }

        @Override
        public int read() throws IOException {
            int character = reader.read();
            if (character == -1) {
                handleEmptyReader();
            }
            return character;
        }

        private void handleEmptyReader() throws IOException {
            if (ioException != null) {
                throw ioException;
            } else if (runtimeException != null) {
                throw runtimeException;
            }
        }

        @Override
        public int read(byte[] buffer, int offset, int len) throws IOException {
            if (buffer == null) {
                throw new NullPointerException();
            } else if (offset < 0 || len < 0 || len > buffer.length - offset) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            } else {
                return readNextLine(buffer, offset, len);
            }
        }

        private int readNextLine(byte[] buffer, int offset, int len) throws IOException {
            int c = read();
            if (c == -1) {
                return -1;
            }
            buffer[offset] = (byte) c;

            int i = 1;
            for (; (i < len) && !isCompleteLineWritten(buffer, i - 1); ++i) {
                byte read = (byte) read();
                if (read == -1) {
                    break;
                } else {
                    buffer[offset + i] = read;
                }
            }
            return i;
        }

        private boolean isCompleteLineWritten(byte[] buffer,
            int indexLastByteWritten) {
            byte[] separator = getProperty("line.separator")
                .getBytes(defaultCharset());
            int indexFirstByteOfSeparator = indexLastByteWritten
                - separator.length + 1;
            return indexFirstByteOfSeparator >= 0
                && contains(buffer, separator, indexFirstByteOfSeparator);
        }

        private boolean contains(byte[] array,
            byte[] pattern,
            int indexStart) {
            for (int i = 0; i < pattern.length; ++i) {
                if (array[indexStart + i] != pattern[i]) {
                    return false;
                }
            }
            return true;
        }
    }
}
