package uk.org.webcompere.systemstubs.resource;

import uk.org.webcompere.systemstubs.ThrowingRunnable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Helper functions for test resources
 */
public class Resources {
    /**
     * The execute-around idiom. Prepares a resource, runs the resources and then cleans up
     * @param callable the item to run
     * @param resources the resources to set up
     * @throws Exception on error
     */
    public static <T> T executeAround(Callable<T> callable, TestResource ... resources) throws Exception {
        LinkedList<TestResource> resourcesSetUp = new LinkedList<>();

        try {
            for (TestResource resource : resources) {
                resourcesSetUp.addFirst(resource);
                resource.setup();
            }

            return callable.call();
        } finally {
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
}
