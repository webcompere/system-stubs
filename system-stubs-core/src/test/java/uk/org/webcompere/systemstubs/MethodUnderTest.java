package uk.org.webcompere.systemstubs;

interface MethodUnderTest {
	void accept(
		ThrowingRunnable throwingRunnable
	) throws Exception;
}
