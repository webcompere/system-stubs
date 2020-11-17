package uk.org.webcompere.systemstubs.jupiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field in a test class as a system stub - this makes it get set up
 * automatically around individual tests or, if static, around the whole test fixture
 * @since 1.0.0
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemStub {
}
