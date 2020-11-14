package uk.org.webcompere.systemstubs.resource;

import uk.org.webcompere.systemstubs.ThrowingRunnable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Helper functions for test resources
 */
public class Resources {
    /**
     * Use the execute around idiom with multiple resources
     * @param resources the resources to wrap around the test, in the order to set them up
     * @param <T> the return type
     * @return an {@link Executable} with the {@link Executable#execute} methods on it
     */
    public static <T> Executable with(TestResource ... resources) {
        return new Executable() {
            @Override
            public <T> T execute(Callable<T> callable) throws Exception {
                return Resources.execute(callable, resources);
            }
        };
    }

    /**
     * The execute-around idiom. Prepares a resource, runs the resources and then cleans up. The resources
     * are set up in the order of declaration and tidied in reverse order. Any failure during set up results in
     * a corresponding teardown operation, just in case, but only for those resources that have been set up so far.
     * @param callable the item to run
     * @param resources the resources to set up
     * @throws Exception on error
     */
    public static <T> T execute(Callable<T> callable, TestResource ... resources) throws Exception {
        LinkedList<TestResource> resourcesSetUp = new LinkedList<>();

        try {
            for (TestResource resource : resources) {
                resourcesSetUp.addFirst(resource);
                resource.setup();
            }

            return callable.call();
        } finally {
            executeCleanup(resourcesSetUp);
        }
    }

    public static void executeCleanup(List<TestResource> resourcesSetUp) throws Exception {
        Exception firstExceptionThrownOnTidyUp = null;
        for (TestResource resource : resourcesSetUp) {
            try {
                resource.teardown();
            } catch (Exception e) {
                firstExceptionThrownOnTidyUp = firstExceptionThrownOnTidyUp == null ? e : firstExceptionThrownOnTidyUp;
            }
        }
        if (firstExceptionThrownOnTidyUp != null) {
            throw firstExceptionThrownOnTidyUp;
        }
    }
}
