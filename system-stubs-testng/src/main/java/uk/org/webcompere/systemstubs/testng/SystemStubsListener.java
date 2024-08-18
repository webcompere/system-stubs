package uk.org.webcompere.systemstubs.testng;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import uk.org.webcompere.systemstubs.resource.TestResource;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static uk.org.webcompere.systemstubs.resource.Resources.executeCleanup;

/**
 * Add this to a test class with:
 *
 * <pre>
 * &#064;@Listeners(SystemStubsListener.class)
 * public class MyTestClass {
 *
 * }
 * </pre>
 * This causes any of the system stubs objects, that inherit {@link TestResource} to
 * become active during tests. It will also instantiate any objects not initialized in the
 * initializer list.
 */
public class SystemStubsListener implements IInvokedMethodListener {
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        List<TestResource> stubs = ensureAllStubsAreInstantiated(method);
        if (method.isTestMethod()) {
            try {
                for (TestResource stub: stubs) {
                    stub.setup();
                }
            } catch (Exception e) {
                throw new AssertionError("Could not set up stubs", e);
            }
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            try {
                executeCleanup(getAllStubs(method));
            } catch (Exception e) {
                throw new AssertionError("Could not tidy up stubs", e);
            }
        }
    }

    private static TestResource readSystemStubResource(Field field, Object testObject) {
        if (!TestResource.class.isAssignableFrom(field.getType())) {
            throw new IllegalArgumentException("Cannot use @SystemStub with non TestResource object in field " +
                field.getName() + " this one's a " +
                field.getType().getCanonicalName());
        }
        try {
            makeAccessible(field);
            return (TestResource)field.get(testObject);
        } catch (Exception e) {
            throw new AssertionError("Cannot read field " + field.getName(), e);
        }
    }

    private static List<TestResource> getAllStubs(IInvokedMethod method) {
        Object testObject = method.getTestMethod().getInstance();
        Field[] fields = testObject.getClass().getDeclaredFields();
        return Arrays.stream(fields)
            .filter(field -> field.isAnnotationPresent(SystemStub.class))
            .map(field -> readSystemStubResource(field, testObject))
            .filter(Objects::nonNull)
            .collect(toList());
    }

    private static <T extends AccessibleObject> T makeAccessible(T object) {
        if (!object.isAccessible()) {
            object.setAccessible(true);
        }
        return object;
    }

    private static List<TestResource> ensureAllStubsAreInstantiated(IInvokedMethod method) {
        Object testObject = method.getTestMethod().getInstance();
        Field[] fields = testObject.getClass().getDeclaredFields();
        return Arrays.stream(fields)
            .filter(field -> field.isAnnotationPresent(SystemStub.class))
            .map(field -> instantiateIfNecessary(field, testObject))
            .collect(toList());
    }

    @SuppressFBWarnings(value = "REC_CATCH_EXCEPTION",
        justification = "Generic catch block provided as lots can go wrong when using reflection")
    private static TestResource instantiateIfNecessary(Field field, Object testObject) {
        if (!TestResource.class.isAssignableFrom(field.getType())) {
            throw new IllegalArgumentException("Cannot use @SystemStub with non TestResource object in field " +
                field.getName() + " this one's a " +
                field.getType().getCanonicalName());
        }

        try {
            makeAccessible(field);
            Object currentObject = field.get(testObject);
            if (currentObject == null) {
                Object newInstance = field.getType().getDeclaredConstructor().newInstance();
                field.set(testObject, newInstance);
                return (TestResource) newInstance;
            }
            return (TestResource) currentObject;
        } catch (Exception e) {
            throw new AssertionError("Cannot access field " + field.getName(), e);
        }
    }
}
