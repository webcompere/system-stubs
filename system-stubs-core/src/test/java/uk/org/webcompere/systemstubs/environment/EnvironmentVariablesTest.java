package uk.org.webcompere.systemstubs.environment;

import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnvironmentVariablesTest {
    @Test
    void canCreateNewEnvironmentVariablesObject() {
        new EnvironmentVariables(emptyMap());
    }

    @Test
    void canCreateEnvironmentVariablesObjectWithNoInitialValues() {
        new EnvironmentVariables();
    }

    @Test
    void environmentVariablesAddedDontTakeImmediateEffect() throws Exception {
        new EnvironmentVariables(singletonMap("FOO", "bar"));
        assertThat(System.getenv("FOO")).isNull();
    }

    @Test
    void environmentVariablesCanBeUsed() throws Exception {
        new EnvironmentVariables(singletonMap("FOO", "bar"))
            .execute(() -> assertThat(System.getenv("FOO")).isEqualTo("bar"));
    }

    @Test
    void usingAndProducesNewObjectWithNoLink() throws Exception {
        EnvironmentVariables one = new EnvironmentVariables().and("FOO", "bar");
        EnvironmentVariables two = one.and("BAZ", "buzz");

        one.execute(() -> {
            assertThat(System.getenv("BAZ")).isNull();
            assertThat(System.getenv("FOO")).isEqualTo("bar");
        });

        two.execute(() -> {
            assertThat(System.getenv("BAZ")).isEqualTo("buzz");
            assertThat(System.getenv("FOO")).isEqualTo("bar");
        });
    }

    @Test
    void cannotSetExistingEnvironmentVariableWithAnd() {
        assertThatThrownBy(() -> new EnvironmentVariables()
            .and("foo", "bar")
            .and("foo", "bar"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void usingAndWhileInTheEnvironmentHasNoEffect() throws Exception {
        EnvironmentVariables env = new EnvironmentVariables();
        env.execute(() -> {
            env.and("BONG", "bing");
            assertThat(System.getenv("BONG")).isNull();
        });
    }

    @Test
    void usingAndProducesNewObjectWithNoLinkEvenWhenSetIsUsed() throws Exception {
        EnvironmentVariables one = new EnvironmentVariables().and("FOO", "bar");
        EnvironmentVariables two = one.and("BAZ", "buzz");

        one.set("COLOR", "red");

        one.execute(() -> {
            assertThat(System.getenv("BAZ")).isNull();
            assertThat(System.getenv("FOO")).isEqualTo("bar");
            assertThat(System.getenv("COLOR")).isEqualTo("red");
        });

        two.execute(() -> {
            assertThat(System.getenv("BAZ")).isEqualTo("buzz");
            assertThat(System.getenv("FOO")).isEqualTo("bar");
            assertThat(System.getenv("COLOR")).isNull();
        });
    }

    @Test
    void canSetEnvironmentVariableToApplyWhileActive() throws Exception {
        EnvironmentVariables env = new EnvironmentVariables();
        env.execute(() -> {
            env.set("FOO", "bar");
            assertThat(System.getenv("FOO")).isEqualTo("bar");
            env.set("FOO", "notbar");
            assertThat(System.getenv("FOO")).isEqualTo("notbar");
        });
        assertThat(System.getenv("FOO")).isNull();
        env.execute(() -> {
            assertThat(System.getenv("FOO")).isEqualTo("notbar");
        });
    }

    @Test
    void convenienceConstructorMustHaveEvenParameters() {
        assertThatThrownBy(() -> new EnvironmentVariables("A", "B", "C"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void convenienceConstructorSetsOneVariable() throws Exception {
        new EnvironmentVariables("TOO", "tar")
            .execute(() -> assertThat(System.getenv("TOO")).isEqualTo("tar"));
    }

    @Test
    void convenienceConstructorSetsMultipleVariables() throws Exception {
        new EnvironmentVariables("TOO", "tar", "BOO", "bar")
            .execute(() -> {
                assertThat(System.getenv("TOO")).isEqualTo("tar");
                assertThat(System.getenv("BOO")).isEqualTo("bar");
            });
    }
}
