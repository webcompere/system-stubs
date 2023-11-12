package uk.org.webcompere.systemstubs.environment;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import uk.org.webcompere.systemstubs.internal.ProcessEnvironmentInterceptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.*;
import java.util.jar.JarFile;

import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;

/**
 * This takes control of the environment variables using ByteBuddy. It captures the environment
 * when first used, and defaults to that. When the {@link EnvironmentVariables} mock wishes to provide
 * mocking, the alternative map of variables is put into a stack and set as the current variables used by
 * the interceptor.
 */
@SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED",
    justification = "We need to set up the stub, but interaction is set on construction")
public class EnvironmentVariableMocker {
    private static final Stack<Map<String, String>> REPLACEMENT_ENV = new Stack<>();
    private static final Map<String, String> ORIGINAL_ENV;

    static {
        ORIGINAL_ENV = new HashMap<>(System.getenv());
        try {
            Instrumentation instrumentation = ByteBuddyAgent.install();
            installInterceptorIntoBootLoader(instrumentation);

            var byteBuddy = new ByteBuddy();
            byteBuddy.redefine(Class.forName("java.lang.ProcessEnvironment"))
                .method(isStatic().and(namedOneOf("getenv", "environment", "toEnvironmentBlock")))
                .intercept(MethodDelegation.to(ProcessEnvironmentInterceptor.class))
                .make()
                .load(
                    EnvironmentVariableMocker.class.getClassLoader(),
                    ClassReloadingStrategy.fromInstalledAgent());

            ProcessEnvironmentInterceptor.setEnv(ORIGINAL_ENV);
        } catch (Throwable e) {

            throw new IllegalStateException("Cannot set up environment mocking: " + e.getMessage() +
                ".", e);
        }
    }

    private static void installInterceptorIntoBootLoader(Instrumentation instrumentation) throws IOException {
        File tempFile = File.createTempFile("interceptor",".jar");
        tempFile.deleteOnExit();
        try (FileOutputStream file = new FileOutputStream(tempFile);
            var resourceStream = EnvironmentVariableMocker.class.getClassLoader()
                .getResourceAsStream("system-stubs-interceptor.jar")) {
            resourceStream.transferTo(file);
        }

        instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(tempFile));
    }

    @Deprecated(since = "2.1.5")
    public static void connect(Map<String, String> newEnvironmentVariables) {
        connect(newEnvironmentVariables, Collections.emptySet());
    }

    /**
     * Attach a map as the mutable replacement environment variables for now. This can be done
     * multiple times and each time the replacement will supersede the maps before. Then when {@link #pop()}
     * is called, we'll rollback to the previous.
     * @param newEnvironmentVariables the mutable map - note: this will be populated by the current
     *                                environment
     * @param variablesToRemove a list of variables to take out of the resulting environment variables
     */
    public static void connect(Map<String, String> newEnvironmentVariables, Set<String> variablesToRemove) {
        // add all entries not already present in the new environment variables
        System.getenv().entrySet().stream()
            .filter(entry -> !newEnvironmentVariables.containsKey(entry.getKey()))
            .forEach(entry -> newEnvironmentVariables.put(entry.getKey(), entry.getValue()));
        variablesToRemove.forEach(newEnvironmentVariables::remove);
        REPLACEMENT_ENV.push(newEnvironmentVariables);
        ProcessEnvironmentInterceptor.setEnv(newEnvironmentVariables);
    }

    /**
     * Remove the latest set of mock environment variables. This will run all the way to empty, after which
     * the original implementation of the getenv functions will be called directly again.
     * @return true if mocking has now stopped
     */
    public static synchronized boolean pop() {
        if (!REPLACEMENT_ENV.empty()) {
            REPLACEMENT_ENV.pop();
        }

        if (!REPLACEMENT_ENV.empty()) {
            ProcessEnvironmentInterceptor.setEnv(REPLACEMENT_ENV.peek());
        } else {
            ProcessEnvironmentInterceptor.setEnv(ORIGINAL_ENV);
        }

        return REPLACEMENT_ENV.empty();
    }

    /**
     * A safer form - allows us to remove the specific map that we want to
     * @param theOneToPop the map to remove
     * @return true if removed
     */
    public static synchronized boolean remove(Map<String, String> theOneToPop) {
        var result = REPLACEMENT_ENV.remove(theOneToPop);

        if (!REPLACEMENT_ENV.empty()) {
            ProcessEnvironmentInterceptor.setEnv(REPLACEMENT_ENV.peek());
        } else {
            ProcessEnvironmentInterceptor.setEnv(ORIGINAL_ENV);
        }

        return result;
    }
}
