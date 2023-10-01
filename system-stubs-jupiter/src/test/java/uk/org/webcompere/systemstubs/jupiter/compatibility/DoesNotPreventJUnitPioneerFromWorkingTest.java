package uk.org.webcompere.systemstubs.jupiter.compatibility;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledForJreRange(max = JRE.JAVA_16)
class DoesNotPreventJUnitPioneerFromWorkingTest {

    @Nested
    @ExtendWith(SystemStubsExtension.class)
    static class StubsWorks {
        @SystemStub
        private EnvironmentVariables variables;

        @Test
        void canStubVariable() {
            variables.set("MACHINE", "hot");

            assertThat(System.getenv("MACHINE")).isEqualTo("hot");
        }
    }

    @Nested
    @SetEnvironmentVariable(key="MACHINE", value="COLD")
    static class PioneerWorks {
        @Test
        void canPioneerVariable() {
            assertThat(System.getenv("MACHINE")).isEqualTo("cold");
        }
    }

}
