# System Stubs

[![Build status](https://ci.appveyor.com/api/projects/status/r943gjn189rlxts9/branch/master?svg=true)](https://ci.appveyor.com/project/ashleyfrieze/system-stubs/branch/master)
 [![codecov](https://codecov.io/gh/webcompere/system-stubs/branch/master/graph/badge.svg?token=J0N9VCXFQ1)](https://codecov.io/gh/webcompere/system-stubs)

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

### Core

```xml
<dependency>
  <groupId>uk.org.webcompere</groupId>
  <artifactId>system-stubs-core</artifactId>
  <version>1.1.0</version>
</dependency>
```

### JUnit 4 Plugin

```xml
<dependency>
  <groupId>uk.org.webcompere</groupId>
  <artifactId>system-stubs-junit4</artifactId>
  <version>1.1.0</version>
</dependency>
```

### JUnit 5 Extension

```xml
<dependency>
  <groupId>uk.org.webcompere</groupId>
  <artifactId>system-stubs-jupiter</artifactId>
  <version>1.1.0</version>
</dependency>
```

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
and turned off again with `teardown`. Where possible, these objects also support
reconfiguration while they're active, allowing you to set environment variables within
a test, for example:

```java
EnvironmentVariables env = new EnvironmentVariables();
env.execute(() -> {
    env.set("a", "b");
    // this has affected the environment
});
```

**Note:** there are two versions of the `execute` method in `Executable` allowing
the test code to return values, or not.

**Note:** the JUnit4 and JUnit5 plugins automatically handle multiple test stubs created outside
of the test methods.

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

### Sources of `Properties` for `EnvironmentVariables` and `SystemProperties`

Once you have constructed an `EnvironmentVariables` or `SystemProperties` object, you can use the `set` method to apply properties. If these objects are presently _active_
then the values are applied to the running environment immediately, otherwise they
are kept until the object is activated either by `execute` or within the JUnit test
lifecycle, as part of the JUnit 4 or JUnit 5 plugins.

There is a `set` function for name/value pairs, and also a `set` function that
takes `Map<Object, Object>`, which is the base class of `Properties`. There are helper
functions within `PropertySource` for loading `Properties` from file or resources.

So you can initialise one of these stubs from a resource:

```java
// Note, we have statically imported `PropertySource.fromResource`
EnvironmentVariables env = new EnvironmentVariables()
    .set(fromResource("test.properties"))
    .execute(() -> {... test code });
```

Or from a file:

```java
SystemProperties props = new SystemProperties();
props.execute(() -> {
    // do something

    // now set the system properties from a file
    props.set(fromFile("src/test/resources/test.properties"));
});
```

Or from a map:

```java
// Map.of is available in later Java versions
// ImmutableMap.of from Guava is a similar alternative
EnvironmentVariables env = new EnvironmentVariables();
env.execute(() -> {
    // do something

    // now set some environment variables
    env.set(Map.of("VAL", "value1",
                  "VAL2", "value"));
});
```

The name/value pair constructors in both `EnvironmentVariables` and `SystemProperties` are probably easier than using `set` with a `Map` where it's possible to use them.

The `EnvironmentVariables` and `SystemProperties` objects both accept a `Properties` object via their constructor. It's a question of preference whether to use the constructor or set method:

```java
new EnvironmentVariables()
   .set(fromFile("somefile"));

// vs

new EnvironmentVariables(fromFile("someFile"));
```

If you have the properties to set in memory already as a series of `String` objects in the `name=value` format used by properties files, you can use `LinesAltStream` to provide them to the property loader as an `InputStream`:

```java
EnvironmentVariables env = new EnvironmentVariables()
   .set(fromInputStream(new LinesAltStream("PROXY_HOSTS=foo.bar.com")))
   .execute(() -> {
      // the PROXY_HOSTS environment variable is set here
   });
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
the classes `SystemOut` and `SystemErr` which can be used independently.

##### Providing a Single Output Target

When creating a `SystemOut` object, its constructor can be passed the relevant `Output` types of `NoopStream`, `DisallowWriteStream` or `TapStream`. The default is `TapStream`.

Once output has been captured, all of the objects provide functions for getting the text
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

**Note:** As the `SystemOut`, `SystemErr` and `SystemErrAndOut` classes are also derived from `Output`, they have friendlier methods on them for reading the text that was sent to the output. E.g. `getLines`
which returns a stream of lines, separated from the text captured by the system line separator.

**Note:** The `withSystemErrAndOut` method on the facade constructs a `SystemErrAndOut` object for use with the `execute` method and for assertion via `getLines` or `getText`:

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

##### Multiple Outputs - Tapping `System.out` in Combinations

One of the advantages of tapping the `System.out` and `System.err` streams is that the tests can assert what was output. However, seeing the output of the application during the test can also be helpful for debugging.

There is an alternative way to provide the `Output` object for the `SystemOut`, `SystemErr` and `SystemErrAndOut` objects to use. If you pass an `OutputFactory` then this can be used to construct the final `Output` object using the original `OutputStream` that was being used before the stubbing started. This allows the output to reuse the original console `PrintStream` alongside any other streams.

There is also a `MultiplexOutput` class which is able to direct the output to more than one `Output` object. **Note:** the first `Output` will be the default used for `getText` and related operations. Though if you created and stored references to all of the `Output` objects in the multiplex, you can interact with them directly.

Though the lower level classes may be useful for building custom configurations, the most common options are within the `OutputFactories` class.

For example, you can both capture `System.out` and allow it to continue writing to the console like this:

```java
SystemOut systemOut = new SystemOut(tapAndOutput());
systemOut.execute(() -> System.out.println("I write to the console and the tap"));
assertThat(systemOut.getLines()).containsExactly("I write to the console and the tap");
```

The `tapAndOutput` function produces a multiplex of both `TapStream` and writing to the original stream.

When using the `execute` method (as above), rather than any of the JUnit plugins, it's also possible to capture the output to a file using `writeToFile` as the `OutputFactory`:

```java
File target = new File(tempDir, "file");
new SystemOut(ofMultiplePlusOriginal(writeToFile(target)))
  .execute(() -> {
    System.out.println("This is going into a file");
  });

assertThat(target).hasContent("This is going into a file" + System.lineSeparator());
```

While the file output does not depend itself on the original stream, it hooks its creation and closure of the `OutputStream` into the lifecycle of the execute method.

Technically this could also be used with the JUnit plugins, but the written file could not be accessed within the test that it logged.

The `OutputFactories` class provides various methods for adding together multiple `Output` objects. The `Output.fromStream` and `Output.fromCloseableStream` methods provide `Output` wrappers of your own `OutputStream` objects.

**Note:** you can compose multiple `Output` objects or multiple `OutputFactory` objects. If you have a mixture, then convert the `Output` objects into `OutputFactory` objects using `Output.factoryOfSelf`.

There are some worked examples in the [tests of `OutputFactories`](system-stubs-core/src/test/java/uk/org/webcompere/systemstubs/stream/output/OutputFactoriesTest.java).

##### Asserting Log Output

The `SystemOut` stub allows logging output to be captured in situations where the logging framework is configured to write to the console.

Let's say the code under test contained this line:

```java
LOGGER.info("Saving to database");
```

We could imagine testing that with a `SystemOut` object:

```java
SystemOut systemOut = new SystemOut();

// then either in the execute method, or via JUnit4 or JUnit5 integration

realCode.doThingThatLogs();
assertThat(systemOut.getLines())
  .anyMatch(line -> line.contains("Saving to database"));
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
* May need to login to `gpg` to set the passphrase
* With env variables
  - `JAVA_HOME` set to JDK8
  - `GPG_TTY=$(tty)`
  - `GPG_AGENT_INFO`
* With the nexus credentials set in the `.m2/settings.xml`
* Run `mvn clean -Dgpg.executable=gpg -Prelease-sign-artifacts -Dgpg.passphrase=<biscuit leet> release:prepare release:perform`
* Update the installation guide in the README
* Push a new version to the README
