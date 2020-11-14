# System Stubs JUnit4

This is the System Stubs native equivalent to [System Rules](https://stefanbirkner.github.io/system-rules/index.html).

The code for this version originated with the [System Lambda](https://github.com/stefanbirkner/system-lambda)
project and have been rebuilt from the ground up for this version.

## Environment Variables

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

## `SystemPropertiesRule`

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

## `SystemExitRule`

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

## `SystemOutRule` and `SystemErrRule`

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

## `SystemInRule`

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
