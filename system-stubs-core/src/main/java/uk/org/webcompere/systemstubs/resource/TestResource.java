package uk.org.webcompere.systemstubs.resource;

/**
 * A test resource is something that can be set up at the start of a test and
 * torn down at the end.
 */
public interface TestResource {
    /**
     * Prepare the resource for testing
     * @throws Exception on error starting
     */
    void setup() throws Exception;

    /**
     * Clean up the resource
     * @throws Exception on error cleaning up
     */
    void teardown() throws Exception;
}
