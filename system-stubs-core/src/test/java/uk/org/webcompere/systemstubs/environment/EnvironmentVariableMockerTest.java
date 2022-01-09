package uk.org.webcompere.systemstubs.environment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentVariableMockerTest {

    @AfterEach
    void clearUp() {
        while(!EnvironmentVariableMocker.pop()) {}
    }

    @Test
    void environmentStartsClean() {
        assertThat(System.getenv().get("foo")).isNull();
    }

    @Test
    void whenAttachEnvironmentThenGetAllVariables() {
        Map<String, String> newMap = new HashMap<>();
        EnvironmentVariableMocker.connect(newMap);

        assertThat(newMap).isNotEmpty();
    }

    @Test
    void canAddAnEnvironmentVariable() {
        Map<String, String> newMap = new HashMap<>();
        EnvironmentVariableMocker.connect(newMap);

        assertThat(System.getenv().get("foo")).isNull();

        newMap.put("foo", "bar");

        assertThat(System.getenv().get("foo")).isEqualTo("bar");
        assertThat(System.getenv("foo")).isEqualTo("bar");
    }

    @Test
    void whenEnvironmentContainsVariableAndMapIsConnectedTheMapWins() {
        Map<String, String> newMap = new HashMap<>();
        EnvironmentVariableMocker.connect(newMap);
        newMap.put("foo", "bar");

        Map<String, String> overridingMap = new HashMap<>();
        overridingMap.put("foo", "bong");
        EnvironmentVariableMocker.connect(overridingMap);

        assertThat(overridingMap.get("foo")).isEqualTo("bong");
    }

    @Test
    void whenAddNullThenItIsNotInGetEnvReturn() {
        Map<String, String> newMap = new HashMap<>();
        EnvironmentVariableMocker.connect(newMap);
        newMap.put("foo", null);

        assertThat(System.getenv()).doesNotContainKey("foo");
    }
}
