package uk.org.webcompere.systemstubs.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

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
}
