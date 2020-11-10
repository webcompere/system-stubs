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

## System Properties Rule

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
