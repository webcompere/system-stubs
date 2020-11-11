package uk.org.webcompere.systemstubs.security;

/**
 * Can be thrown by a Security Manager to stop execution of the test. The {@link SecurityManagerStub}
 * will recognise that the test hasn't failed, but the code under test needs to stop.
 */
public class AbortExecutionException extends SecurityException {
    private static final long serialVersionUID = 159678654L;
}
