package uk.org.webcompere.systemstubs.testng;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field in a test class as a system stub - this causes the {@link SystemStubsListener} to activate
 * it during tests. It also causes the field to become instantiated if left uninitialized
 * @since 1.0.0
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemStub {
}
