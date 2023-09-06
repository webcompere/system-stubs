package uk.org.webcompere.systemstubs.environment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.System.getenv;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.condition.OS.*;

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
    void theVariablesAreVisibleToWorkerThreads() throws Exception {
        Map<String, String> result = new HashMap<>();

        Map<String, String> newMap = new HashMap<>();
        newMap.put("foo", "bar");
        EnvironmentVariableMocker.connect(newMap);

        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            result.put("foo", getenv("foo"));
            latch.countDown();
        }).start();

        latch.await(10, TimeUnit.SECONDS);

        assertThat(result).containsEntry("foo", "bar");
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

    @Test
    void processBuilderEnvironmentIsAffectedByMockEnvironment() {
        Map<String, String> newMap = new HashMap<>();
        newMap.put("FOO", "bar");
        EnvironmentVariableMocker.connect(newMap);
        assertThat(new ProcessBuilder().environment()).containsEntry("FOO", "bar");
    }

    @EnabledOnOs({ MAC, LINUX })
    @Test
    void processBuilderEnvironmentWhenLaunchingNewApplicationIsAffected() throws Exception {
        Map<String, String> newMap = new HashMap<>();
        newMap.put("FOO", "bar");

        EnvironmentVariableMocker.connect(newMap);

        ProcessBuilder builder = new ProcessBuilder("/usr/bin/env");
        builder.environment().put("BING", "bong");
        String output = executeProcessAndGetOutput(builder);

        assertThat(output).contains("FOO=bar");
        assertThat(output).contains("BING=bong");
    }

    @EnabledOnOs({ MAC, LINUX })
    @Test
    void canLaunchWithDefaultEnvironmentAndNothingIsAdded() throws Exception {
        Map<String, String> newMap = new HashMap<>();

        EnvironmentVariableMocker.connect(newMap);

        ProcessBuilder builder = new ProcessBuilder("/usr/bin/env");
        String output = executeProcessAndGetOutput(builder);
        assertThat(output).doesNotContain("FOO=bar");
        assertThat(output).doesNotContain("BING=bong");
    }

    @EnabledOnOs(WINDOWS)
    @Test
    void windowsProcessBuilderEnvironmentCanReadEnvironmentFromMock() throws Exception {
        Map<String, String> newMap = new HashMap<>();
        newMap.put("FOO", "bar");

        EnvironmentVariableMocker.connect(newMap);

        ProcessBuilder builder = new ProcessBuilder(Arrays.asList("cmd.exe", "/c", "set"));
        builder.environment().put("BING", "bong");
        String output = executeProcessAndGetOutput(builder);

        assertThat(output).contains("FOO=bar");
        assertThat(output).contains("BING=bong");
    }

    @EnabledOnOs(WINDOWS)
    @Test
    void windowsProcessBuilderEnvironmentCanReadEnvironmentFromMockAlone() throws Exception {
        Map<String, String> newMap = new HashMap<>();
        newMap.put("FOO", "bar");

        EnvironmentVariableMocker.connect(newMap);

        ProcessBuilder builder = new ProcessBuilder(Arrays.asList("cmd.exe", "/c", "set"));
        String output = executeProcessAndGetOutput(builder);

        assertThat(output).contains("FOO=bar");
    }

    private String executeProcessAndGetOutput(ProcessBuilder builder) throws IOException {
        Process process = builder.start();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return bufferedReader.lines().collect(Collectors.joining("\n"));
        } catch (Throwable t) {
            System.err.println(t.getMessage());
            throw t;
        } finally {
            process.destroy();
        }
    }
}
