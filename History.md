# History

Based on the excellent work by Stefan Birkner in [System Rules](https://stefanbirkner.github.io/system-rules/index.html) and [System Lambda](https://github.com/stefanbirkner/system-lambda) this is a remix
of the core techniques, to allow them to be used more flexibly.

No longer limited to just being a JUnit4 rule (SystemRules) and available as a JUnit 5 plugin, this version is intended to increase usability and configurability, in a way that diverges from the original trajectory of **System Lambda**.

This version comes with the [agreement](https://github.com/stefanbirkner/system-lambda/issues/9) of the original author. The original author bears no responsibility for this version.

### Differences

The main aims of this version:

- Enable environment variables to be set before child test suites execute
  - allow environment details to be set in _beforeAll_ or _beforeEach_ hooks
  - as can be necessary for Spring tests
- Support JUnit4 and JUnit5 plugins
  - reduce test boilerplate
- Provide more configuration and fluent setters
- Modularise the code
- Standardise testing around _Mockito_ and _AssertJ_ 
- Standardise around the Java Library as much as possible

