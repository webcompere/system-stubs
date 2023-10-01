# System Stubs TestNG

Provides some automatic instantiation of System Stubs objects during the test lifecycle.

```xml
<dependency>
  <groupId>uk.org.webcompere</groupId>
  <artifactId>system-stubs-testng</artifactId>
  <version>2.1.3</version>
</dependency>
```

## Options

### Stub Without the Plugin
System Stubs Core can be used with TestNG as it is framework agnostic.

We can call the `setup` method on any of the stubs in a before method, and `teardown` in the after method.

```java
private EnvironmentVariables environmentVariables = new EnvironmentVariables();

@BeforeTest
public void beforeTest() throws Exception {
    environmentVariables.set("setinbefore", "yes");

    environmentVariables.setup();
}

@AfterTest
public void afterTest() throws Exception {
    environmentVariables.teardown();
}
```

With this code, we'd expect tests to be able to modify the runtime environment by manipulating the
`environmentVariables` object, and we'd expect the tests to see the environment variables set in the `beforeTest`
function; in this example, `setinbefore` was set to `yes`.

Similarly, we can use `setup` and `teardown` methods of a `TestResources` such as `EnvironmentVariables` inside a test case, or use the `SystemStubs` wrapper methods such as
`withEnvironmentVariables`. See the [main documentation](../README.md) for more on the execute around pattern.

### Using the Plugin

The plugin:

- Automatically instantiates system stubs objects before they're first used by a TestNG annotated method
- Activates the objects during tests
- Turns the objects off after tests

Usage:

```java
@Listeners(SystemStubsListener.class)
public class CaptureSystemOutTest {

    // We can ininitialise this object with `new` here, or
    // leave it uninitialized for the plugin to do it for us
    @SystemStub
    private SystemOut out;

    @BeforeTest
    public void beforeTest() {
        out.clear();
    }

    @Test
    public void canReadThingsSentToSystemOut() {
        // simulate the system under test writing to std out
        System.out.println("Can I assert this?");

        assertThat(out.getText()).isEqualTo("Can I assert this?\n");
    }
}
```

> Note: in this instance we've used the `SystemOut` stub. We've had to remember to call its `clear` method as it
> will be shared between tests.
> Note: we may prefer to initialize our stub objects to give them configuration or initial values without using
> a `@BeforeTest` function.

We can use any of the core system stubs classes such as:

- `EnvironmentVariables` - for overriding the environment variables
- `SystemProperties` - for temporarily overwriting system properties and then restoring them afterwards
- `SystemOut` - for tapping the `System.out`
- ... and so on

All we need to do is:

- Add the `@Listeners(SystemStubsListener.class)` annotation to our TestNG test class (using an array with {} if we have other listeners)
- Add a field for each System Stub we want to use
- Annotate that field with the `@SystemStubs` annotation

### Benefits of the Plugin

With the plugin, there's less boilerplate to write. Any exception handling is also covered by the plugin - or at
least, we don't have to explicitly add `throws` to any of our methods that set up or teardown a stub.

However, the plugin is simple and opinionated. For fine-grained control of the stubs, the direct method
may sometimes be preferable.

## Feedback

This TestNG module is incubating. Please raise issues with examples if it proves to have issues in practice.
