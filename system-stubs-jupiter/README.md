# System Stubs JUnit Jupiter

Supports two methods of providing System Stub `TestResource` objects to
JUnit 5 unit test.

```xml
<dependency>
  <groupId>uk.org.webcompere</groupId>
  <artifactId>system-stubs-jupiter</artifactId>
  <version>2.1.0</version>
</dependency>
```

## Extension

Add the extension to a test class:

```java
@ExtendWith(SystemStubsExtension.class)
```

This will resolve parameters and fields.

## Using the Extension
### By Parameter

Provides a stub to the unit test which has already captured the necessary
restore settings and is active. Will tidy up after the test:

```java
@Test
void method1(SystemProperties properties) {
    properties.set("prop1", "prop1");
    assertThat(System.getProperty("prop1")).isEqualTo("prop1");
}
```

Supports all the System Stub objects that can be constructed without
parameters, including `SystemProperties` and `EnvironmentVariables`.

### By Field

Any field marked with `@SystemStub` will be instantiated and activated before
each test if not already instantiated. If already instantiated it
will be activated. At the end of each test it will be torn down.

This allows for the environment to be built in `@BeforeEach` methods, for example.

It also allows for the whole class to repeatedly clean the environment. E.g.

```java
@SystemStub
private SystemProperties systemProperties;

@Test
void someTest() {
    // this test cannot damage the system properties
    // because changes made via System.setProperty
    // or systemProperties.set will be undone after the test
}
```

The `@SystemStub` annotation is required for the automatic use of the
system stub objects so that it is also possible to manually set up and tear down
the objects in other parts of the lifecycle as required.

### Fields with Single Test Instance

In cases where `@TestInstance(TestInstance.Lifecycle.PER_CLASS)` is used,
the system stub objects in the test instance fields are shared across multiple tests.

This means that the following is possible:

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SomeTest {

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
}
```

### Static Fields

When a System Stub object is used with a static field, it is
automatically created and activated around the whole test class:

```java
@SystemStub
private static EnvironmentVariables testWideVariables = new EnvironmentVariables(
    "some", "value",
    "other", "setting");

@BeforeAll
static void beforeAll() {
   // maybe work out some additional environment variables
   // to set here and use testWideVariables.set(...)
}

@Test
void thisTestRunsWithTheEnvironment() {
    // and if this test uses testWideVariables.set(..)
    // the environment variable will be shared with subsequent
    // tests
}
```
## Use Cases

A strong use case for dynamic environment variables and system properties is
when performing Spring testing using dynamic resources such as Testcontainers
or WireMock, which can run on random ports.

Similarly, a particular system under test may automatically apply proxy
settings from the environment variables as part of its static initialization.

This plugin, or the manual use of the `setup` and `teardown` methods
on the System Stub resource objects can allow the environment
to be set at the right moment before other tests depend on it.

See [the SpringBoot example test](src/test/java/uk/org/webcompere/systemstubs/jupiter/examples/SpringAppWithDynamicPropertiesTest.java) for an example.

## Examples

- [EnvironmentVariables declared before test](src/test/java/uk/org/webcompere/systemstubs/jupiter/examples/WithEnvironmentVariables.java)
- [EnvironmentVariables, System Properties and TappingSystemOut](src/test/java/uk/org/webcompere/systemstubs/jupiter/examples/MultipleTestResources.java) - demonstrating
multiple resources being set up with their defaults
- [Injecting Resources by Parameter](src/test/java/uk/org/webcompere/systemstubs/jupiter/examples/InjectByParameter.java)
- [SpringBoot dynamic property setting](src/test/java/uk/org/webcompere/systemstubs/jupiter/examples/SpringAppWithDynamicPropertiesTest.java) - including use of `SystemOut` to capture log output
- [System.Exit managed by the extension](src/test/java/uk/org/webcompere/systemstubs/jupiter/examples/SystemExitUseCase.java)

## Extensibility

This plugin may also be used with home-made test resources so long
as they subclass `TestResource`.

Example:

```java
public class CustomResource implements TestResource {
   // ... override setup/teardown to manage resource
}


// test
@ExtendWith(SystemStubsExtension.class)
class SomeTest {
    @SystemStub
    private CustomResource customResource; // assuming there's a default constructor

    @Test
    void someTest() {
       // access the resource
    }
}
```

**Note: The extension requires the test resource to provide a default constructor to enable it to create
instances for parameter injection and automatic field creation.**
