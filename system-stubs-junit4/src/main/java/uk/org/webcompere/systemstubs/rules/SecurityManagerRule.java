package uk.org.webcompere.systemstubs.rules;

import uk.org.webcompere.systemstubs.rules.internal.SystemStubTestRule;
import uk.org.webcompere.systemstubs.security.SecurityManagerStub;

/**
 * Set an alternative security manager within tests
 */
public class SecurityManagerRule extends SecurityManagerStub<SecurityManager> implements SystemStubTestRule {
    /**
     * Default constructor - use this when the security manager will be set in-flight
     */
    public SecurityManagerRule() {
    }

    /**
     * Construct with a specific security manager to use
     * @param securityManager the security manager to use
     */
    public SecurityManagerRule(SecurityManager securityManager) {
        super(securityManager);
    }
}
