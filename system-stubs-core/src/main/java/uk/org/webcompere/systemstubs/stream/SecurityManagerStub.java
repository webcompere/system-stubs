package uk.org.webcompere.systemstubs.stream;

import uk.org.webcompere.systemstubs.resource.SingularTestResource;

import static java.lang.System.getSecurityManager;
import static java.lang.System.setSecurityManager;

/**
 * Switch the security manager for an alternative
 */
public class SecurityManagerStub extends SingularTestResource {
    private SecurityManager originalSecurityManager;
    private SecurityManager newSecurityManager;

    /**
     * Construct with the security manager to substitute
     * @param newSecurityManager the new manager
     */
    public SecurityManagerStub(SecurityManager newSecurityManager) {
        this.newSecurityManager = newSecurityManager;
    }

    @Override
    protected void doSetup() throws Exception {
        originalSecurityManager = getSecurityManager();
        setSecurityManager(newSecurityManager);
    }

    @Override
    protected void doTeardown() throws Exception {
        setSecurityManager(originalSecurityManager);
    }
}
