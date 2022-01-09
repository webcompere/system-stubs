package uk.org.webcompere.systemstubs.environment;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Stack;

import static java.util.stream.Collectors.toMap;

/**
 * This takes control of the environment variables using {@link Mockito#mockStatic}. While there
 * are maps of mock environment variables, the getenv functions will be directed at them. Otherwise,
 * the original ProcessEnvironment method will be called.
 */
@SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED",
    justification = "We need to set up the stub, but interaction is set on construction")
public class EnvironmentVariableMocker {
    private static final Stack<Map<String, String>> REPLACEMENT_ENV = new Stack<>();

    static {
        try {
            Class<?> typeToMock = Class.forName("java.lang.ProcessEnvironment");
            Mockito.mockStatic(typeToMock, invocationOnMock -> {
                if (REPLACEMENT_ENV.empty() || !invocationOnMock.getMethod().getName().equals("getenv")) {
                    return invocationOnMock.callRealMethod();
                }
                Map<String, String> currentMockedEnvironment = REPLACEMENT_ENV.peek();
                if (invocationOnMock.getMethod().getParameterCount() == 0) {
                    return filterNulls(currentMockedEnvironment);
                }
                return currentMockedEnvironment.get(invocationOnMock.getArgument(0, String.class));
            });
        } catch (Throwable e) {
            throw new IllegalStateException("Cannot set up environment mocking: " + e.getMessage() +
                ". This may be a result of not having the right mockito-inline installed, " +
                "or may be down to a Java internals change.", e);
        }
    }

    private static Map<String, String> filterNulls(Map<String, String> currentMockedEnvironment) {
        return currentMockedEnvironment.entrySet()
            .stream()
            .filter(entry -> entry.getValue() != null)
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Attach a map as the mutable replacement environment variables for now. This can be done
     * multiple times and each time the replacement will supersede the maps before. Then when {@link #pop()}
     * is called, we'll rollback to the previous.
     * @param newEnvironmentVariables the mutable map - note: this will be populated by the current
     *                                environment
     */
    public static void connect(Map<String, String> newEnvironmentVariables) {
        // add all entries not already present in the new environment variables
        System.getenv().entrySet().stream()
            .filter(entry -> !newEnvironmentVariables.containsKey(entry.getKey()))
            .forEach(entry -> newEnvironmentVariables.put(entry.getKey(), entry.getValue()));
        REPLACEMENT_ENV.push(newEnvironmentVariables);
    }

    /**
     * Remove the latest set of mock environment variables. This will run all the way to empty, after which
     * the original implementation of the getenv functions will be called directly again.
     * @return true if mocking has now stopped
     */
    public static boolean pop() {
        if (!REPLACEMENT_ENV.empty()) {
            REPLACEMENT_ENV.pop();
        }
        return REPLACEMENT_ENV.empty();
    }

    /**
     * A safer form - allows us to remove the specific map that we want to
     * @param theOneToPop the map to remove
     * @return true if removed
     */
    public static boolean remove(Map<String, String> theOneToPop) {
        return REPLACEMENT_ENV.remove(theOneToPop);
    }
}
