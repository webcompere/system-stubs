package uk.org.webcompere.systemstubs.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.systemstubs.resource.PropertySource.fromResource;

@RunWith(Enclosed.class)
public class EnvironmentVariablesRuleTest {
    public static class UsingSingleArgumentConstructor {
        @Rule
        public EnvironmentVariablesRule environmentVariablesRule = new EnvironmentVariablesRule()
            .and("SOME", "value")
            .set("OTHER", "thing");

        @Test
        public void hasEnvironmentVariablesFromInitializer() {
            assertThat(System.getenv("SOME")).isEqualTo("value");
            assertThat(System.getenv("OTHER")).isEqualTo("thing");
        }

        @Test
        public void canMakeChangeToEnvironment() {
            assertThat(System.getenv("FOO")).isNull();
            environmentVariablesRule.set("FOO", "bar");
            assertThat(System.getenv("FOO")).isEqualTo("bar");
        }

        @Test
        public void canMakeAlternativeChangeToEnvironment() {
            assertThat(System.getenv("FOO")).isNull();
            environmentVariablesRule.set("FOO", "baz");
            assertThat(System.getenv("FOO")).isEqualTo("baz");
        }
    }

    public static class UsingMultipleArgumentConstructor {
        @Rule
        public EnvironmentVariablesRule environmentVariablesRule = new EnvironmentVariablesRule(
            "SOME", "value2",
            "OTHER", "thing2");

        @Test
        public void withMultipleEnvironmentVariablesSetByConstructor() {
            assertThat(System.getenv("SOME")).isEqualTo("value2");
            assertThat(System.getenv("OTHER")).isEqualTo("thing2");
        }
    }

    public static class UsingTestResources {
        @Rule
        public EnvironmentVariablesRule environmentVariablesRule = new EnvironmentVariablesRule()
            .set(fromResource("test.properties"));

        @Test
        public void withMultipleEnvironmentVariablesSetByConstructor() {
            assertThat(System.getenv("value1")).isEqualTo("foo");
        }
    }

    public static class UsingTestResourcesViaConstructor {
        @Rule
        public EnvironmentVariablesRule environmentVariablesRule =
            new EnvironmentVariablesRule(fromResource("test.properties"));

        @Test
        public void withMultipleEnvironmentVariablesSetByConstructor() {
            assertThat(System.getenv("value1")).isEqualTo("foo");
        }
    }
}
