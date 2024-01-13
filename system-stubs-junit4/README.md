# System Stubs JUnit4

This is the System Stubs native equivalent to [System Rules](https://stefanbirkner.github.io/system-rules/index.html).

The code for this version originated with the [System Lambda](https://github.com/stefanbirkner/system-lambda)
project and has been rebuilt from the ground up for this version.

```xml
<dependency>
  <groupId>uk.org.webcompere</groupId>
  <artifactId>system-stubs-junit4</artifactId>
  <version>2.1.6</version>
</dependency>
```

## Using JUnit 4 Test Rules

Each of the stubs in System Stubs Core has a corresponding test rule subclass in this module.
You can use a rule by declaring it in your test class with the `@Rule` annotation:

```java
public class SomeTestClass {
    @Rule
    public EnvironmentVariablesRule rule = new EnvironmentVariablesRule();
}
```

**Note:** the rule object must be declared `public`.

### `static` / `@ClassRule`

If it's necessary for the stubbing to stay active between tests, or perhaps to be set up earlier than
some other static code that the test calls, then you can use the `@ClassRule` annotation
on a static member of the class:

```java
public class SomeTestClass {
    @ClassRule
    public static EnvironmentVariablesRule rule =
        new EnvironmentVariablesRule("NO_PROXY", "http://www.google.com");
}
```

**Note:** while it may be useful to declare a stubbing rule and then set it up while it is active,
you may wish to ensure your rule has all of its initial values on construction when using it with
`@ClassRule`. The constructors and fluent setters on each rule object should help with that.

There's an example test class with `@ClassRule` [here](src/test/java/uk/org/webcompere/systemstubs/rules/ClassRuleTest.java).

### Warning on `static` fields

If some of your production code initializes static fields from the environment or system properties,
then your test needs to set the correct values for those properties before the class of the production code
is first referenced. It will not be possible for the field to get a new test value in a different
unit test either, as all the tests are likely to be run in the same JVM which initializes the static
fields once only.

## Available JUnit 4 Rules

### Environment Variables

Provides a plugin to allow environment variables to be managed during a test:

```java
@Rule
public EnvironmentVariablesRule rule = new EnvironmentVariables(
    "foo", "bar",
    "enabled", "true");

@Test
public void someTest() {
    // as the variable was defined in the rule object, we can
    // expect it to be present during the test
    assertThat(System.getenv("foo")).isEqualTo("bar");

    // we can also set variables during the test
    rule.set("var", "value");
    assertThat(System.getenv("var")).isEqualTo("value");
}
```

### `SystemPropertiesRule`

A plugin which restores system properties to the state they were
before the test started. Allows for properties to be defined
before the set to be applied to the system.

```java
@Rule
public SystemPropertiesRule rule = new SystemPropertiesRule("property", "value");

@Test
public void someTest() {
    // expect the property set in the constructor to be available here

    // expect any calls to SystemPropertiesRule.set
    // or System.setProperty to change the properties but
    // be reverted after the test
}
```

### `SystemExitRule`

`SystemExitRule` will prevent the JVM terminating during a test if something calls
`System.exit`. Instead, an `AbortExecutionException` will be thrown, which can be caught.

The rule object contains the exit code.

```java
@Rule
public SystemExitRule systemExitRule = new SystemExitRule();

@Test
public void theCodeExitsButTheProgramDoesnt() {
    assertThatThrownBy(() -> {
        // some test code performs an exit
        System.exit(92);
    }).isInstanceOf(AbortExecutionException.class);

    assertThat(systemExitRule.getExitCode()).isEqualTo(92);
}

@Test
public void noSystemExit() {
    assertThat(systemExitRule.getExitCode()).isNull();
}
```

### `SystemOutRule` and `SystemErrRule`

These rules capture the output to `System.out` and `System.err` during tests. By default
they use the `TapStream` output, which records output. They can also be constructed with the
`NoopStream` to discard all output (and save memory and logs), or the `DisallowWriteStream` which
will cause an assertion error if there is an output.

```java
@Rule
public SystemOutRule systemOutRule = new SystemOutRule();

@Test
public void whenWriteToSystemOutItCanBeSeen() {
    // imagine the code under test did this
    System.out.println("I am the system");

    // then the test code can read it
    assertThat(systemOutRule.getLines()).containsExactly("I am the system");
}
```

Both `SystemErrRule` and `SystemOutRule` can be used in the same test, though more than
one instance of the same rule will cause only the most recent to be active.

Here's an example of how to set up the `DisallowWriteStream`.

```java
@Rule
public SystemOutRule noOutput = new SystemOutRule(new DisallowWriteStream());

@Test
public void someTest() {
    // calling something that did a System.out.println would result in AssertionError
}
```

The `SystemErrAndOutRule` taps both system error and output with a
single `Output` object. This defaults to `TapStream` but can
be specified in the constructor.

**Note:** the factory methods in `OutputFactories` allow more complex output chains to be constructed, including tapping the output while allowing it to still about in `stdout`:

```java
// the output is still tapped for checking what was written
// but it can also be seen on the console
@Rule
public SystemOutRule systemOutRule = new SystemOutRule(tapAndOutput());
```

For more examples, see the [core documentation](../README.md).

**Note:** writing output to file with the JUnit rule may work, but would be hard to assert on, since the file would still be open during the test case doing the writing.

### `SystemInRule`

`SystemInRule` applies a new input stream to `System.in` within tests.
It subclasses `SystemIn` which allows the stream to be changed while
the test is running and allows the stub to throw a specific exception
when a read operation is performed on it after it runs out of content.

A simple example of `SystemInRule` would be to provide multiple
lines of text to `System.in` separated by the system's default separator:

```java
@Rule
public SystemInRule systemInRule = new SystemInRule("line1", "line2");

@Test
public void canReadText() {
    assertThat(readLinesFromSystemIn(2)).containsExactly("line1", "line2");
}
```

The source of lines/bytes can be pretty much anything, including a filestream
or arbitrary `Stream<String>`.

### `SecurityManagerRule`

An alternative `SecurityManager` can be set either by providing it
to the constructor of `SecurityManagerRule`:

```java
@Rule
public SecurityManagerRule securityManagerRule = new SecurityManagerRule(mock(SecurityManager.class));

@Test
public void securityManagerAlreadyMocked() {
    // the security manager must be a mockito mock for this to work
    then(System.getSecurityManager())
        .should(never())
        .checkLink(any());
}
```

or it can be set using `setManager` with an alternative manager created
during the test:

```java
// a similar mechanism to SystemExitRule
willThrow(new RuntimeException("don't go"))
    .given(mockManager)
    .checkExit(anyInt());

securityManagerRule.setSecurityManager(mockManager);

assertThatThrownBy(() -> System.exit(123))
    .hasMessage("don't go");

then(mockManager).should().checkExit(123);
```

## Custom Rules

You can use System Stubs to help you make your own rules by inheriting `SystemStubTestRule`.

In practice, this is no better than subclassing JUnit's own `ExternalResource`
to create a custom rule, but may be useful if you have already created a subclass
of `TestResource` or `SingularTestResource` in order to use System Stubs functionality in
other situations, and need to make these work in JUnit 4 also.

## When The Rules Don't Fit

There are extensive tests demonstrating each of the JUnit rules. However, in some situations, you may need finer control of the System Stub objects. You can always mix using rules and core objects via the `execute` pattern. It may also be easier to combine methods from `SystemStubs` with some common rules.

It is worth noting that putting common set up into a `@Rule` object is useful unless there is nothing common between test cases. In that instance, consider having multiple test classes for each configuration, or using only the most essential common setup within rules, and adding the `execute` pattern for variations.
