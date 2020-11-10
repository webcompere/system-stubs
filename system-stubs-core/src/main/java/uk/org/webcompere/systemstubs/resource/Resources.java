package uk.org.webcompere.systemstubs.resource;

import java.util.LinkedList;
import java.util.concurrent.Callable;

/**
 * Helper functions for test resources
 */
public class Resources {
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

    private static void executeCleanup(LinkedList<TestResource> resourcesSetUp) throws Exception {
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
