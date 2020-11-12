package uk.org.webcompere.systemstubs.rules;

import uk.org.webcompere.systemstubs.rules.internal.SystemStubTestRule;
import uk.org.webcompere.systemstubs.security.SystemExit;

/**
 * Add to a test to catch System exit events
 */
public class SystemExitRule extends SystemExit implements SystemStubTestRule {
}
