package uk.org.webcompere.systemstubs.security;

/**
 * A {@link uk.org.webcompere.systemstubs.resource.TestResource} which provides the exit code called when it was active.
 * Gives access to the {@link NoExitSecurityManager} object inside via {@link SecurityManagerStub#getSecurityManager()}
 */
public class SystemExit extends SecurityManagerStub<NoExitSecurityManager> {
    /**
     * What was the exit code provided if System.exit was called.
     * @return exit code or <code>null</code> if no exit called
     */
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

