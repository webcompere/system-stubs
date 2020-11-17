# System Stubs

[![Build status](https://ci.appveyor.com/api/projects/status/r943gjn189rlxts9/branch/master?svg=true)](https://ci.appveyor.com/project/ashleyfrieze/system-stubs/branch/master)
 [![codecov](https://codecov.io/gh/webcompere/system-stubs/branch/master/graph/badge.svg)](https://codecov.io/gh/webcompere/system-stubs)

## Overview
System Stubs is used to test code which depends on methods in `java.lang.System`.

It is published under the
[MIT license](http://opensource.org/licenses/MIT) and requires at least Java 8.

It is divided into:

- `system-stubs-core` - can be used stand-alone to stub system resources around test code
  - Using the `SystemStubs` facade to build and execute stubs around test code
  - Using the subclasses of `TestResource`, like `EnvironmentVariables` or `SystemIn` to create stubs
  and then execute test code inside them
- [`system-stubs-junit4`](system-stubs-junit4/README.md) - a set of JUnit4 rules that activate the stubs around test code
- [`system-stubs-jupiter`](system-stubs-jupiter/README.md) - a JUnit 5 extension that automatically injects
System Stubs into JUnit 5 tests.

## History
Based on the excellent work by Stefan Birkner in
[System Rules](https://stefanbirkner.github.io/system-rules/index.html) and [System Lambda](https://github.com/stefanbirkner/system-lambda) this is a remix
of the core techniques, to allow them to be used more
flexibly.

No longer limited to just being a JUnit4 rule (SystemRules)
and available as a JUnit 5 plugin, this version is intended to increase usability
and configurability, in a way that diverges from the original trajectory of **System Lambda**.

This version comes with the [agreement](https://github.com/stefanbirkner/system-lambda/issues/9) of the original author.
The original author bears no responsibility for this version.

### Differences

The main aims of this version:

- Enable environment variables to be set before child test suites execute
  - allow environment details to be set in _beforeAll_ or _beforeEach_ hooks
  - as can be necessary for Spring tests
- Support JUnit4 and JUnit5 plugins
  - reduce test boilerplate
- Provide more configuration and fluent setters
- Modularise the code
- Standardise testing around _Mockito_ and _AssertJ_ (removing
home-made alternatives)

## Installation

... tbc - when published to maven central

## Usage with Execute Around

In order to support migration from [System Lambda](https://github.com/stefanbirkner/system-lambda), and
to enable reuse of the original unit tests, the `SystemStubs` facade supports the original
[execute around](https://java-design-patterns.com/patterns/execute-around/) idiom.

To use the `SystemStubs` facade:

```java
import static uk.org.webcompere.systemstubs.SystemStubs.*;
```

## Usage of individual System Stubs

You can declare a system stub object:

```java
EnvironmentVariables environmentVariables = new EnvironmentVariables("a", "b");
```

Then you can configure it and execute your test code inside it:

```java
environmentVariables.set("c", "d")
    .execute(() -> { ... some test code that gets the environment variables ... });
```

Where necessary, each of the System Stub objects can be manually activated with `setup`
and turned off again with `teardown`. Where possible, these object also support
reconfiguration while they're active, allowing you to set environment variables within
a test, for example:

```java
EnvironmentVariables env = new EnvironmentVariables();
env.execute(() -> {
    env.set("a", "b");
    // this has affected the environment
});
```

### Using multiple stubs

While you can set up stubs inside the `execute` method of a parent stub:

```java
new EnvironmentVariables("a", "b")
    .execute(() -> {
        new SystemProperties("j", "k")
            .execute(() -> { ... has env and properties ... });
    });
```

There is a more convenient way to use multiple stubs together:

```java
EnvironmentVariables env = new EnvironmentVariables("a", "b");
SystemProperties props = new SystemProperties("f", "g");
Resources.execute(() -> { .. some test code .. },
    env, props);
```

The convenience method `Resource.with` may make this read more cleanly:

```java
with(new EnvironmentVariables("HTTP_PROXY", ""),
    new SystemProperties("http.connections", "123"))
    .execute(() -> executeTestCode());
```

**Note:** there are two versions of the `execute` method in `Executable` allowing
the test code to return values, or not.

**Note:** the JUnit4 and JUnit5 plugins automatically handle multiple test stubs created outside
of the test methods.

## Exception Handling

As the `execute` methods can be used with code that throws exceptions, they
declare `throws Exception` so your tests need to declare `throws Exception`, even if
the code under the test doesn't use checked exceptions.

This is a good argument for using the JUnit4 or JUnit5 plugins, where you do not
need to specifically turn the stubbing on via the `execute` method.

## Available Stubs

### System.exit

Command-line applications terminate by calling `System.exit` with some status
code. If you test such an application then the JVM that executes the test exits
when the application under test calls `System.exit`.

#### With `SystemStubs`

The method `catchSystemExit` returns the status code of the
`System.exit` call rather than ending the JVM

```java
@Test
void application_exits_with_status_42() throws Exception {
  int statusCode = catchSystemExit(() -> {
    System.exit(42);
  });
  assertEquals(42, statusCode);
}
```

The method `catchSystemExit` throws an `AssertionError` if the code under test
does not call `System.exit`. Therefore your test fails with the failure message
"System.exit has not been called."

#### `SystemExit` Class

The `SystemExit` class can be used just to ignore a System exit:

```java
new SystemExit()
    .execute(() -> {
        System.exit(0);
    });

// execution continues without error
```

Or an instance can be used to capture the return code, or whether there was an
exit at all.

```java
SystemExit exit = new SystemExit();
exit.execute(() -> {
        System.exit(0);
    });

assertThat(exit.getExitCode()).isEqualTo(0);

// the exit code will be `null` if no System.exit was called
```

### Environment Variables

#### With `SystemStubs`

The method `withEnvironmentVariable` allows you to set environment variables
within your test code that are removed after your code under test is executed.

```java
@Test
void execute_code_with_environment_variables() throws Exception {
  List<String> values = withEnvironmentVariable("first", "first value")
    .and("second", "second value")
    .execute(() -> asList(
      System.getenv("first"),
      System.getenv("second")
    ));
  assertEquals(asList("first value", "second value"), values);
}
```

#### With `EnvironmentVariables`

Create an object of `EnvironmentVariables` and use `execute`:

```java
List<String> values = new EnvironmentVariables("first", "first value")
    .set("second", "second value")
    .execute(() -> asList(
         System.getenv("first"),
         System.getenv("second")
       ));
     assertEquals(asList("first value", "second value"), values);
```

**Note:** the `SystemStubs` facade creates an identical object and `set` is a
mutable version of the `and` method used in the first example.

**Note:** calling `set` on `EnvironmentVariables` from inside `execute` will
affect the runtime environment. Calling it outside of execution will store the
value for writing into the environent within `execute`.

### System Properties

#### With `SystemStubs`

The method `restoreSystemProperties` guarantees that after executing the test
code each System property has the same value as before. Therefore you can
modify System properties inside of the test code without having an impact on
other tests.

```java
@Test
void execute_code_that_manipulates_system_properties() throws Exception {
  restoreSystemProperties(() -> {
    System.setProperty("some.property", "some value");
    //code under test that reads properties (e.g. "some.property") or
    //modifies them.
  });

  //Here the value of "some.property" is the same like before.
  //E.g. it is not set.
}
```

#### With `SystemProperties`

A `SystemProperties` object allows you to set the system properties that will be provided
within `execute`. It provides a `set` method which writes to the System while the
object is _active_, though any other set operations that are performed with
`System.setProperty` are also reset on clean up:

```java
SystemProperties someProperties = new SystemProperties(
    "foo", "bar",
    "foz", "boz");
someProperties.execute(() -> {
    // here we expect the properties to have been set

    // we can also call "set" on the "someProperties"
    // to set more system properties - these will be
    // remembered for reuse later with that object

    // any calls to System.setProperty will be undone when
    // "execute" is finished
});

// here the system properties are reverted
```

### Stubbing `System.out` and `System.err`

#### With `SystemStubs`

Command-line applications usually write to the console. If you write such
applications you need to test the output of these applications. The methods
`tapSystemErr`, `tapSystemErrNormalized`, `tapSystemOut` and
`tapSystemOutNormalized` and `tapSystemErrAndOut` allow you to tap the text that is written to
`System.err`/`System.out`. The methods with the suffix `Normalized` normalize
line breaks to `\n` so that you can run tests with the same assertions on
different operating systems.

```java
@Test
void application_writes_text_to_System_err() throws Exception {
  String text = tapSystemErr(() -> {
    System.err.print("some text");
  });
  assertEquals("some text", text);
}

@Test
void application_writes_mutliple_lines_to_System_err() throws Exception {
  String text = tapSystemErrNormalized(() -> {
    System.err.println("first line");
    System.err.println("second line");
  });
  assertEquals("first line\nsecond line\n", text);
}

@Test
void application_writes_text_to_System_out() throws Exception {
  String text = tapSystemOut(() -> {
    System.out.print("some text");
  });
  assertEquals("some text", text);
}

@Test
void application_writes_mutliple_lines_to_System_out() throws Exception {
  String text = tapSystemOutNormalized(() -> {
    System.out.println("first line");
    System.out.println("second line");
  });
  assertEquals("first line\nsecond line\n", text);
}
```

`System.err` and `System.out` can be directed to a single stream:

```java
@Test
void application_writes_text_to_System_err_and_out() throws Exception {
  String text = tapSystemErrAndOut(() -> {
    System.err.print("text from err");
    System.out.print("text from out");
  });
  assertEquals("text from errtext from out", text);
}
```

You can assert that nothing is written to `System.err`/`System.out` by wrapping
code with the function
`assertNothingWrittenToSystemErr`/`assertNothingWrittenToSystemOut`. E.g. the
following tests fail:

```java
@Test
void fails_because_something_is_written_to_System_err() throws Exception {
  assertNothingWrittenToSystemErr(() -> {
    System.err.println("some text");
  });
}

@Test
void fails_because_something_is_written_to_System_out() throws Exception {
  assertNothingWrittenToSystemOut(() -> {
    System.out.println("some text");
  });
}
```

If the code under test writes text to `System.err`/`System.out` then it is
intermixed with the output of your build tool. Therefore you may want to avoid
that the code under test writes to `System.err`/`System.out`. You can achieve
this with the function `muteSystemErr`/`muteSystemOut`. E.g. the following tests
don't write anything to `System.err`/`System.out`:

```java
@Test
void nothing_is_written_to_System_err() throws Exception {
  muteSystemErr(() -> {
    System.err.println("some text");
  });
}

@Test
void nothing_is_written_to_System_out() throws Exception {
  muteSystemOut(() -> {
    System.out.println("some text");
  });
}
```

#### Using `SystemErr`, `SystemOut` and `SystemErrAndOut`

The methods on the facade provide some useful shortcuts, but there are also
the classes `SystemOut` and `SystemErr` which can be instantiated
with the relevant `Output` types of `NoopStream`, `DisallowWriteStream` or `TapStream`. They
default to using `TapStream`. All of these objects provide functions for getting the text
that arrived at the stream, sliced into lines or whole.

You can plug in an alternative output by implementing your own `Output` subclass.

**Note:** The `DisallowWriteStream` cannot capture text as any writes stop the text with an error.
The `NoopStream` does not capture text, so it useful for saving memory/log files during a test.

Example:

```java
SystemOut systemOut = new SystemOut();
systemOut.execute(() -> System.out.print("hello world"));
assertThat(systemOut.getText()).isEqualTo("hello world");
```

The objects can be reused and have a `clear` function to clear captured text between usages.

**Note:** As the `SystemOut`, `SystemErr` and `SystemErrAndOut` classes are also derived from `Output`
they have friendlier methods on them for reading the text that was sent to the output. E.g. `getLines`
which returns a stream of lines, separated from the text captured by the system line separator.

**Note:** The `withSystemErrAndOut` method on the facade also provides a `SystemErrAndOut` object for finer control
over assertion:

```java
// finer control over assertion can be made using the SystemErrAndOut object
@Test
void construct_system_err_and_out_tap() throws Exception {
    SystemErrAndOut stream = withSystemErrAndOut(new TapStream());
    stream.execute(() -> {
        System.err.println("text from err");
        System.out.println("text from out");
    });
    assertThat(stream.getLines())
        .containsExactly("text from err","text from out");
}
```

### Stubbing `System.in`

Interactive command-line applications read from `System.in`. You can
supply the application with input at test time as lines of text, delimited by
the system line separator, or by hooking `System.in` up to a
specific `InputStream`.

#### With `SystemStubs`

You can specify
the lines that are available from `System.in` with the method
`withTextFromSystemIn`

```java
@Test
void Scanner_reads_text_from_System_in() throws Exception {
  withTextFromSystemIn("first line", "second line")
    .execute(() -> {
      Scanner scanner = new Scanner(System.in);
      assertEquals("first line", scanner.nextLine());
      assertEquals("second line", scanner.nextLine());
    });
}
```

For a complete test coverage you may also want to simulate `System.in` throwing
exceptions when the application reads from it. You can specify such an
exception (either `RuntimeException` or `IOException`) after specifying the
text. The exception will be thrown by the next `read` after the text has been
consumed.

```java
@Test
void System_in_throws_IOException() throws Exception {
  withTextFromSystemIn("first line", "second line")
    .andExceptionThrownOnInputEnd(new IOException())
    .execute(() -> {
      Scanner scanner = new Scanner(System.in);
      scanner.nextLine();
      scanner.nextLine();
      assertThrownBy(
        IOException.class,
        () -> scanner.readLine()
      );
  });
}

@Test
void System_in_throws_RuntimeException() throws Exception {
  withTextFromSystemIn("first line", "second line")
    .andExceptionThrownOnInputEnd(new RuntimeException())
    .execute(() -> {
      Scanner scanner = new Scanner(System.in);
      scanner.nextLine();
      scanner.nextLine();
      assertThrownBy(
        RuntimeException.class,
        () -> scanner.readLine()
      );
    });
}
```

You might also write a test that throws an exception immediately by not providing any
text.

```java
withTextFromSystemIn()
  .andExceptionThrownOnInputEnd(...)
  .execute(() -> {
    Scanner scanner = new Scanner(System.in);
    assertThrownBy(
      ...,
      () -> scanner.readLine()
    );
  });
```

The `SystemStubs` implementation only allows you to specify
text for stubbing `System.in`, the `SystemIn` object is more
configurable.

#### `SystemIn` and `AltInputStream`

The `SystemIn` object allows you to compose your own input text. You can
use it with any `InputStream`.

E.g.

```java
SystemIn systemIn = new SystemIn(new FileInputStream("someTestFile"));
systemIn.execute(() -> {
    // code that uses System.in
});
```

The `SystemIn` object can be manipulated to throw an exception
when the calling code reads from `System.in` when it has run out of text:

```java
new SystemIn("some text in the input")
   .andExceptionThrownOnInputEnd(new IOException("file is broken"))
   .execute(() -> {
      // some test code
   });
```

`SystemIn` can be constructed with lines of text,
or any instance of `AltStream` or `InputStream` you wish to create.

The lines of input are automatically separated by the system line separator
via the `LinesAltStream` object. But the alternative `TextAltStream` can be used
where the input is already formatted and should not have an extra line breaks added.

While hardcoded lists of strings are often perfect sources of test
data, `LinesAltStream` also allows for more custom use cases, for example,
`SystemIn` could be hooked up to a random input generator:

```java
new SystemIn(new LinesAltStream(
    Stream.generate(() -> UUID.randomUUID().toString())))
    .execute(() -> {
        // this test code will be provided with an unlimited
        // series of lines in System.in containing GUIDs
    });
```

### Stubbing `SecurityManager`

#### With `SystemStubs`

The function `withSecurityManager` lets you specify the `SecurityManager` that
is returned by `System.getSecurityManger()` while your code under test is
executed.

```java
@Test
void execute_code_with_specific_SecurityManager() throws Exception {
  SecurityManager securityManager = new ASecurityManager();
  withSecurityManager(
    securityManager,
    () -> {
      //code under test
      //e.g. the following assertion is met
      assertSame(
        securityManager,
        System.getSecurityManager()
      );
    }
  );
}
```

After `withSecurityManager(...)` is executed`System.getSecurityManager()`
returns the original security manager again.

#### With `SecurityManagerStub`

The `SecurityManagerStub` allows you to substitute the system security manager with
another. Provide the alternative manager via the constructor.

```java
new SecurityManagerStub(otherManager)
    .execute(() -> { ... test code ... });
```

This is used internally by the stubbing for `System.exit`.

## Contributing

You have two options if you have a feature request, found a bug or
simply have a question.

* [Write an issue.](https://github.com/webcompere/system-stubs/issues/new)
* Create a pull request. (See [Understanding the GitHub Flow](https://guides.github.com/introduction/flow/index.html))

## Development Guide

System Stubs is built with [Maven](http://maven.apache.org/). If you
want to contribute code then

* Please write a test for your change.
* Ensure that you didn't break the build by running `mvnw test`.
* Fork the repo and create a pull request. (See [Understanding the GitHub Flow](https://guides.github.com/introduction/flow/index.html))

The basic coding style is described in the
[EditorConfig](http://editorconfig.org/) file `.editorconfig`.

System Stubs is built with Appveyor: [![Build status](https://ci.appveyor.com/api/projects/status/r943gjn189rlxts9?svg=true)](https://ci.appveyor.com/project/ashleyfrieze/system-stubs)

## Release Guide

* Move the snapshot version number if necessary using
  [Semantic Versioning 2.0.0 Standard](http://semver.org/).
* With `gpg` installed
* With env variables
  - `JAVA_HOME` set to JDK8
  - `GPG_TTY=$(tty)`
  - `GPG_AGENT_INFO`
* With the nexus credentials set in the `.m2/settings.xml`
* Run `mvn -Prelease-sign-artifacts clean -Dgpg.passphrase=<passphrase> release:prepare release:perform -f pom.xml`
* Update the installation guide in the README
* Push a new version to the README
