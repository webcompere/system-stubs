package uk.org.webcompere.systemstubs.stream;

import uk.org.webcompere.systemstubs.ThrowingRunnable;
import uk.org.webcompere.systemstubs.resource.SingularTestResource;
import uk.org.webcompere.systemstubs.stream.input.*;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.System.setIn;

/**
 * A stub that defines the text provided by {@code System.in}. The methods
 * {@link #andExceptionThrownOnInputEnd(IOException)} and
 * {@link #andExceptionThrownOnInputEnd(RuntimeException)} can be used to
 * simulate a {@code System.in} that throws an exception.
 * The specified behaviour of {@code System.in} is applied to an
 * arbitrary piece of code that is provided to {@link #execute(ThrowingRunnable)}.
 */
public class SystemIn extends SingularTestResource {
    private IOException ioException;
    private RuntimeException runtimeException;
    private InputStream originalIn;
    private AltInputStream altInputStream;

    public SystemIn(String... lines) {
        this(new LinesAltStream(lines));
    }

    public SystemIn(InputStream inputStream) {
        this(new DecoratingAltStream(inputStream));
    }

    public SystemIn(AltInputStream altInputStream) {
        this.altInputStream = altInputStream;
    }

    /**
     * Fluent setter to set the input stream
     * @param inputStream stream to use
     * @return <code>this</code> for fluent use
     */
    public SystemIn setInputStream(InputStream inputStream) {
        return setInputStream(new DecoratingAltStream(inputStream));
    }

    /**
     * Fluent setter to set the input stream
     * @param altInputStream stream to use
     * @return <code>this</code> for fluent use
     */
    public SystemIn setInputStream(AltInputStream altInputStream) {
        if (isActive()) {
            setIn(altInputStream);
        }
        this.altInputStream = altInputStream;
        return this;
    }

    /**
     * Sets an exception that is thrown after the text is read.
     * @param exception the {@code IOException} to be thrown.
     * @return the {@code SystemInStub} itself.
     * @throws IllegalStateException if a {@code RuntimeException} was
     *     already set by {@link #andExceptionThrownOnInputEnd(RuntimeException)}
     */
    public SystemIn andExceptionThrownOnInputEnd(IOException exception) {
        if (altInputStream.contains(ThrowAtEndStream.class)) {
            throw new IllegalStateException("You cannot call" +
                " andExceptionThrownOnInputEnd(IOException) because" +
                " andExceptionThrownOnInputEnd has" +
                " already been called.");
        }

        setInputStream(new ThrowAtEndStream(altInputStream, exception));

        return this;
    }

    /**
     * Sets an exception that is thrown after the text is read.
     * @param exception the {@code RuntimeException} to be thrown.
     * @return the {@code SystemInStub} itself.
     * @throws IllegalStateException if an {@code IOException} was already
     *     set by {@link #andExceptionThrownOnInputEnd(IOException)}
     */
    public SystemIn andExceptionThrownOnInputEnd(RuntimeException exception) {
        if (altInputStream.contains(ThrowAtEndStream.class)) {
            throw new IllegalStateException("You cannot call" +
                " andExceptionThrownOnInputEnd(RuntimeException) because" +
                " andExceptionThrownOnInputEnd has" +
                " already been called.");
        }

        setInputStream(new ThrowAtEndStream(altInputStream, exception));

        return this;
    }

    @Override
    protected void doSetup() throws Exception {
        originalIn = System.in;
        setIn(altInputStream);
    }

    @Override
    protected void doTeardown() throws Exception {
        setIn(originalIn);
        altInputStream.close();
    }
}
