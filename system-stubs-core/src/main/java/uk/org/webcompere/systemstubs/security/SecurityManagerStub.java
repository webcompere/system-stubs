package uk.org.webcompere.systemstubs.security;

import uk.org.webcompere.systemstubs.resource.SingularTestResource;

import java.util.concurrent.Callable;

/**
 * Switch the security manager for an alternative
 */
public class SecurityManagerStub<T extends SecurityManager> extends SingularTestResource {
    private SecurityManager originalSecurityManager;
    private T securityManager;

    /**
     * Default constructor for subclasses that will provide a create method on the fly
     */
    public SecurityManagerStub() {
        this(null);
    }

    /**
     * Construct with the security manager to substitute. If null, then one will be created using
     * the factory method at setup time.
     * @param securityManager a security manager to use while active
     */
    public SecurityManagerStub(T securityManager) {
        this.securityManager = securityManager;
    }

    /**
     * Get the security manager that this class uses when active
     * @return the security manager
     */
    public T getSecurityManager() {
        return securityManager;
    }

    /**
     * Called if we need to wipe the current security manager
     */
    protected void clearSecurityManager() {
        this.securityManager = null;
    }

    /**
     * Set the current security manager
     * @param securityManager the manager to set
     */
    public void setSecurityManager(T securityManager) {
        this.securityManager = securityManager;
        if (isActive()) {
            System.setSecurityManager(securityManager);
        }
    }

    @Override
    protected void doSetup() throws Exception {
        originalSecurityManager = System.getSecurityManager();

        if (securityManager == null) {
            securityManager = createSecurityManager();
        }

        setSecurityManager(securityManager);
    }

    @Override
    protected void doTeardown() throws Exception {
        System.setSecurityManager(originalSecurityManager);
    }

    /**
     * Override to create a specific security manager
     * @return a new security manager - can be null
     */
    protected T createSecurityManager() {
        return null;
    }

    /**
     * Overridden to notice the abort exception that marks exit tests and others where we
     * want to tap into a stop in processing to find the exit code etc.
     * @param callable the callable to execute
     * @param <R> return type
     * @return the result of the callable or null if there was an early abort
     * @throws Exception on exceptions that are not {@link AbortExecutionException}
     */
    @Override
    public <R> R execute(Callable<R> callable) throws Exception {
        try {
            return super.execute(callable);
        } catch (AbortExecutionException ignoreAbortExecution) {
            // stop the test early and return
            return null;
        }
    }
}
