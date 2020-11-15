package uk.org.webcompere.systemstubs.jupiter;

import org.junit.jupiter.api.extension.*;
import uk.org.webcompere.systemstubs.resource.TestResource;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

import static java.lang.reflect.Modifier.isStatic;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotatedFields;
import static org.junit.platform.commons.util.ReflectionUtils.makeAccessible;
import static org.junit.platform.commons.util.ReflectionUtils.tryToReadFieldValue;
import static uk.org.webcompere.systemstubs.resource.Resources.executeCleanup;

/**
 * Use with {@link org.junit.jupiter.api.extension.ExtendWith} to add automatic processing of
 * {@link uk.org.webcompere.systemstubs.resource.TestResource} objects provided by System Stubs.
 * Parameters to functions will be injected as live test resources, and fields marked as
 * {@link SystemStub} will be active during the test and cleaned up automatically after.
 */
public class SystemStubsExtension implements TestInstancePostProcessor,
    TestInstancePreDestroyCallback, ParameterResolver, AfterEachCallback,
    BeforeAllCallback, AfterAllCallback {

    private LinkedList<TestResource> activeResources = new LinkedList<>();

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) throws Exception {
        setupFields(testInstance.getClass(), testInstance, not(SystemStubsExtension::isStaticField));
    }

    @Override
    public void preDestroyTestInstance(ExtensionContext extensionContext) throws Exception {
        Object testInstance = extensionContext.getTestInstance().get();

        cleanupFields(testInstance.getClass(), testInstance, not(SystemStubsExtension::isStaticField));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return TestResource.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) throws ParameterResolutionException {
        try {
            // create using default constructor, turn it on and remember it for cleanup
            TestResource resource = (TestResource)parameterContext.getParameter().getType().newInstance();
            resource.setup();

            activeResources.addFirst(resource);
            return resource;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ParameterResolutionException("Failure to call default constructor of TestResource of type " +
                parameterContext.getParameter().getType().getCanonicalName() +
                ". The type should have a public default constructor.", e);
        } catch (Exception e) {
            throw new ParameterResolutionException("Cannot start test resource: " + e.getMessage(), e);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        executeCleanup(activeResources);
        activeResources.clear();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        cleanupFields(context.getRequiredTestClass(), null, SystemStubsExtension::isStaticField);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        setupFields(context.getRequiredTestClass(), null, SystemStubsExtension::isStaticField);
    }

    private void setup(Field field, Object testInstance) throws Exception {
        if (!TestResource.class.isAssignableFrom(field.getType())) {
            throw new IllegalArgumentException("Cannot use @SystemStub with non TestResource object");
        }
        makeAccessible(field);
        getInstantiatedTestResource(field, testInstance)
            .setup();
    }

    private TestResource getInstantiatedTestResource(Field field, Object testInstance) {
        return tryToReadFieldValue(field, testInstance)
                .toOptional()
                .map(val -> (TestResource)val)
                .orElseGet(() -> assignNewInstanceToField(field, testInstance));
    }

    private TestResource assignNewInstanceToField(Field field, Object testInstance) {
        try {
            TestResource resource = (TestResource)field.getType().newInstance();
            field.set(testInstance, resource);
            return resource;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private void setupFields(Class<?> clazz, Object testInstance, Predicate<Field> predicate) throws Exception {
        for (Field field : findAnnotatedFields(clazz, SystemStub.class, predicate)) {
            setup(field, testInstance);
        }
    }

    private void cleanupFields(Class<?> clazz, Object testInstance, Predicate<Field> predicate) throws Exception {
        LinkedList<TestResource> active = new LinkedList<>();
        findAnnotatedFields(clazz, SystemStub.class, predicate)
            .stream()
            .map(field -> tryToReadFieldValue(field, testInstance).toOptional())
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(item -> (TestResource)item)
            .forEach(active::addFirst);

        executeCleanup(active);
    }

    private static boolean isStaticField(Field f) {
        return isStatic(f.getModifiers());
    }

    // if only Java8 had thought to have this.. the equivalent of the Predicate.not from
    // later versions
    private static <T> Predicate<T> not(Predicate<T> predicate) {
        return predicate.negate();
    }
}
