package uk.org.webcompere.systemstubs.jupiter;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

import static org.assertj.core.api.Assertions.assertThat;

@Nested
@ExtendWith(SystemStubsExtension.class)
public class SystemStubsExtensionStaticClassTest {

    @SystemStub
    private static EnvironmentVariables environmentVariables;

    @BeforeAll
    static void beforeAll() {
        environmentVariables.set("someglobal", "value");
    }

    @Test
    void canAccessFromStaticContext() {
        assertThat(System.getenv("someglobal")).isEqualTo("value");
    }

    @Nested
    class NestedClass {
        @Test
        void hasEnvironmentOfParent() {
            assertThat(System.getenv("someglobal")).isEqualTo("value");
        }
    }
}
