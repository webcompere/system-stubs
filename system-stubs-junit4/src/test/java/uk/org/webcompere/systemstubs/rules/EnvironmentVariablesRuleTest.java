package uk.org.webcompere.systemstubs.rules;

import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

public class EnvironmentVariablesRuleTest {

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
