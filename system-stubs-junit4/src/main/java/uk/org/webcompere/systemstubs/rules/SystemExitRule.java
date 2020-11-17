package uk.org.webcompere.systemstubs.rules;

import uk.org.webcompere.systemstubs.rules.internal.SystemStubTestRule;
import uk.org.webcompere.systemstubs.security.SystemExit;

/**
 * Add to a test to catch System exit events
 * @since 1.0.0
 */
public class SystemExitRule extends SystemExit implements SystemStubTestRule {
}
