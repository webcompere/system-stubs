package uk.org.webcompere.systemstubs.jupiter;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.properties.SystemProperties;
import uk.org.webcompere.systemstubs.security.AbortExecutionException;
import uk.org.webcompere.systemstubs.security.SystemExit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SystemStubsExtensionTest {

    @ExtendWith(SystemStubsExtension.class)
    @TestMethodOrder(MethodOrderer.Alphanumeric.class)
    @Nested
    class Fields {
        @SystemStub
        private EnvironmentVariables environmentVariables = new EnvironmentVariables("try", "this");

        @SystemStub
        private SystemProperties systemProperties;

        @Test
        void method1_whenRunningHasAccessToVariable() {
            assertThat(System.getenv("try")).isEqualTo("this");

            environmentVariables.set("private", "private");
            assertThat(System.getenv("private")).isEqualTo("private");
        }

        @Test
        void method2_canUseSystemPropertiesAsItIsCreatedAndManaged() {
            systemProperties.set("foo", "bar");
            assertThat(System.getProperty("foo")).isEqualTo("bar");
        }

        @Test
        void method3_hasCleanPropertiesAndEnvironment() {
            assertThat(System.getenv("private")).isNull();
            assertThat(System.getProperty("foo")).isNull();
        }
    }

    @ExtendWith(SystemStubsExtension.class)
    @Nested
    class FieldWithoutAnnotation {
        private EnvironmentVariables environmentVariables = new EnvironmentVariables("not", "yet");

        @Test
        void fieldIsNotActivatedWithoutAnnotation() {
            assertThat(System.getenv("not")).isNull();
        }

        @Test
        void butCanStillActivateManually() throws Exception {
            environmentVariables.execute(() -> {
                assertThat(System.getenv("not")).isEqualTo("yet");
            });
        }
    }

    @ExtendWith(SystemStubsExtension.class)
    @TestMethodOrder(MethodOrderer.Alphanumeric.class)
    @Nested
    class CanInjectASystemStubToPutThingsBackAfter {
        @Test
        void method1(SystemProperties properties) {
            properties.set("prop1", "prop1");
            assertThat(System.getProperty("prop1")).isEqualTo("prop1");
        }

        @Test
        void method2_unaffectedByPrevious() {
            assertThat(System.getProperty("prop1")).isNull();
        }

        @Test
        void method3_unaffectedByPreviousAndSetsOwn(SystemProperties properties) {
            assertThat(System.getProperty("prop1")).isNull();
            properties.set("prop2", "prop2");
            assertThat(System.getProperty("prop2")).isEqualTo("prop2");
        }

        @Test
        void method4_setsPropsAndEnv(SystemProperties properties, EnvironmentVariables environmentVariables) {
            properties.set("prop3", "prop3");
            environmentVariables.set("env1", "env1");

            assertThat(System.getProperty("prop3")).isEqualTo("prop3");
            assertThat(System.getenv("env1")).isEqualTo("env1");
        }

        @Test
        void method5_receivesCleanEnvironmentAgain() {
            assertThat(System.getProperty("prop1")).isNull();
            assertThat(System.getProperty("prop2")).isNull();
            assertThat(System.getProperty("prop3")).isNull();
            assertThat(System.getenv("env1")).isNull();
        }
    }

    @Nested
    @ExtendWith(SystemStubsExtension.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.Alphanumeric.class)
    class TestResourceLinkedToLifecycleOfTestInstance {
        @SystemStub
        private EnvironmentVariables environment;

        @Test
        void test1_canSetUpEnvironment() {
            environment.set("shared", "instance");
        }

        @Test
        void test2_canReceiveEnviroment() {
            assertThat(System.getenv("shared")).isEqualTo("instance");
        }

        @Test
        void test3_canUseAdditionalTemporaryEnvironment(EnvironmentVariables alt) {
            alt.set("test3", "only");
            assertThat(System.getenv("test3")).isEqualTo("only");
        }

        @Test
        void test4_cannotReachPrivateVariable() {
            assertThat(System.getenv("test3")).isNull();
        }
    }

    @Nested
    @ExtendWith(SystemStubsExtension.class)
    class SystemExitTest {
        @SystemStub
        private SystemExit systemExit;

        @Test
        void noExitWasCalled() {
            assertThat(systemExit.getExitCode()).isNull();
        }

        @Test
        void exitWasCalled() {
            assertThatThrownBy(() -> {
                System.exit(123);
            }).isInstanceOf(AbortExecutionException.class);

            assertThat(systemExit.getExitCode()).isEqualTo(123);
        }

        @Test
        void injectedByParameter(SystemExit localExit) {
            assertThatThrownBy(() -> {
                System.exit(21);
            }).isInstanceOf(AbortExecutionException.class);


            assertThat(localExit.getExitCode()).isEqualTo(21);

            // the more global object is unaffected
            assertThat(systemExit.getExitCode()).isNull();
        }
    }
}
