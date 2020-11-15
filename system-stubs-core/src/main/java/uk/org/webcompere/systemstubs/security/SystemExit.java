package uk.org.webcompere.systemstubs.security;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A {@link uk.org.webcompere.systemstubs.resource.TestResource} which provides the exit code called when it was active.
 * Gives access to the {@link NoExitSecurityManager} object inside via {@link SecurityManagerStub#getSecurityManager()}.
 * When the {@link NoExitSecurityManager} is in use, any calls to {@link System#exit(int)} are converted
 * to an {@link AbortExecutionException} which the surrounding test can catch.
 */
public class SystemExit extends SecurityManagerStub<NoExitSecurityManager> {
    /**
     * What was the exit code provided if System.exit was called.
     * @return exit code or <code>null</code> if no exit called
     */
    @SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
    public Integer getExitCode() {
        return getSecurityManager() == null ? null : getSecurityManager().getExitCode();
    }

    @Override
    protected NoExitSecurityManager createSecurityManager() {
        return new NoExitSecurityManager(System.getSecurityManager());
    }

    @Override
    protected void doSetup() throws Exception {
        // clear any previous security managers
        clearSecurityManager();
        super.doSetup();
    }
}

