package uk.org.webcompere.systemstubs.rules.internal;

import org.junit.runners.model.Statement;
import uk.org.webcompere.systemstubs.ThrowingRunnable;
import uk.org.webcompere.systemstubs.exception.WrappedThrowable;
import uk.org.webcompere.systemstubs.resource.Resources;
import uk.org.webcompere.systemstubs.resource.TestResource;

/**
 * JUnit4 {@link Statement} helper
 */
public class Statements {
    /**
     * Convert executing the test resource into a statement
     */
    public static Statement toStatement(Statement base, TestResource resource) {
        return new Statement() {
            public void evaluate() throws Throwable {
                try {
                    Resources.execute(ThrowingRunnable.asCallable(base::evaluate), resource);
                } catch (WrappedThrowable t) {
                    throw t.getCause();
                }
            }
        };
    }
}
