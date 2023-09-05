package uk.org.webcompere.systemstubs.environment;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * This takes control of the environment variables using {@link Mockito#mockStatic}. While there
 * are maps of mock environment variables, the getenv functions will be directed at them. Otherwise,
 * the original ProcessEnvironment method will be called.
 */
@SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED",
    justification = "We need to set up the stub, but interaction is set on construction")
public class EnvironmentVariableMocker {
    private static final Stack<Map<String, String>> REPLACEMENT_ENV = new Stack<>();

    private static final Set<String> MOCKED_METHODS = Stream.of("getenv", "environment", "toEnvironmentBlock")
        .collect(toSet());

    static {
        try {
            Class<?> typeToMock = Class.forName("java.lang.ProcessEnvironment");
            Mockito.mockStatic(typeToMock, invocationOnMock -> {
                if (REPLACEMENT_ENV.empty() || !MOCKED_METHODS.contains(invocationOnMock.getMethod().getName())) {
                    return invocationOnMock.callRealMethod();
                }

                if ("toEnvironmentBlock".equals(invocationOnMock.getMethod().getName())) {
                    return simulateToEnvironmentBlock(invocationOnMock);
                }

                Map<String, String> currentMockedEnvironment = getenv();
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

    /**
     * The equivalent of <code>getenv</code> in the original ProcessEnvironment, assuming that
     * mocking is "turned on"
     * @return the current effective environment
     */
    private static Map<String, String> getenv() {
        return REPLACEMENT_ENV.peek();
    }

    /**
     * On Windows, this returns <code>String</code> and converts the inbound Map. On Linux/Mac
     * this takes a second parameter and returns <code>byte[]</code>. Implementations ripped
     * from the JDK source implementation
     * @param invocationOnMock the call to the mocked <code>ProcessEnvironment</code>
     * @return the environment serialized for the platform
     */
    private static Object simulateToEnvironmentBlock(InvocationOnMock invocationOnMock) {
        if (invocationOnMock.getArguments().length == 1) {
            return toEnvironmentBlockWindows(invocationOnMock.getArgument(0));
        } else {
            return toEnvironmentBlockNix(invocationOnMock.getArgument(0),
                invocationOnMock.getArgument(1, int[].class));
        }
    }

    /**
     * Ripped from the JDK implementation
     * @param m the map to convert
     * @return string representation
     */
    private static String toEnvironmentBlockWindows(Map<String, String> m) {
        // Sort Unicode-case-insensitively by name
        List<Map.Entry<String,String>> list = m != null ?
            new ArrayList<>(m.entrySet()) :
            new ArrayList<>(getenv().entrySet());
        Collections.sort(list, (e1, e2) -> NameComparator.compareNames(e1.getKey(), e2.getKey()));

        StringBuilder sb = new StringBuilder(list.size() * 30);
        int cmp = -1;

        // Some versions of MSVCRT.DLL require SystemRoot to be set.
        // So, we make sure that it is always set, even if not provided
        // by the caller.
        final String systemRoot = "SystemRoot";

        for (Map.Entry<String,String> e : list) {
            String key = e.getKey();
            String value = e.getValue();
            if (cmp < 0 && (cmp = NameComparator.compareNames(key, systemRoot)) > 0) {
                // Not set, so add it here
                addToEnvIfSet(sb, systemRoot);
            }
            addToEnv(sb, key, value);
        }
        if (cmp < 0) {
            // Got to end of list and still not found
            addToEnvIfSet(sb, systemRoot);
        }
        if (sb.length() == 0) {
            // Environment was empty and SystemRoot not set in parent
            sb.append('\u0000');
        }
        // Block is double NUL terminated
        sb.append('\u0000');
        return sb.toString();
    }

    // code taken from the original in ProcessEnvironment
    @SuppressFBWarnings({"PZLA_PREFER_ZERO_LENGTH_ARRAYS", "DM_DEFAULT_ENCODING"})
    private static byte[] toEnvironmentBlockNix(Map<String, String> m, int[] envc) {
        if (m == null) {
            return null;
        }
        int count = m.size() * 2; // For added '=' and NUL
        for (Map.Entry<String, String> entry : m.entrySet()) {
            count += entry.getKey().getBytes().length;
            count += entry.getValue().getBytes().length;
        }

        byte[] block = new byte[count];

        int i = 0;
        for (Map.Entry<String, String> entry : m.entrySet()) {
            final byte[] key   = entry.getKey().getBytes();
            final byte[] value = entry.getValue().getBytes();
            System.arraycopy(key, 0, block, i, key.length);
            i += key.length;
            block[i++] = (byte) '=';
            System.arraycopy(value, 0, block, i, value.length);
            i += value.length + 1;
            // No need to write NUL byte explicitly
            //block[i++] = (byte) '\u0000';
        }
        envc[0] = m.size();
        return block;
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

    @SuppressFBWarnings("SE_COMPARATOR_SHOULD_BE_SERIALIZABLE")
    private static final class NameComparator
        implements Comparator<String> {

        public int compare(String s1, String s2) {
            return compareNames(s1, s2);
        }

        public static int compareNames(String s1, String s2) {
            // We can't use String.compareToIgnoreCase since it
            // canonicalizes to lower case, while Windows
            // canonicalizes to upper case!  For example, "_" should
            // sort *after* "Z", not before.
            int n1 = s1.length();
            int n2 = s2.length();
            int min = Math.min(n1, n2);
            for (int i = 0; i < min; i++) {
                char c1 = s1.charAt(i);
                char c2 = s2.charAt(i);
                if (c1 != c2) {
                    c1 = Character.toUpperCase(c1);
                    c2 = Character.toUpperCase(c2);
                    if (c1 != c2) {
                        // No overflow because of numeric promotion
                        return c1 - c2;
                    }
                }
            }
            return n1 - n2;
        }
    }

    // add the environment variable to the child, if it exists in parent
    private static void addToEnvIfSet(StringBuilder sb, String name) {
        String s = getenv().get(name);
        if (s != null) {
            addToEnv(sb, name, s);
        }
    }

    private static void addToEnv(StringBuilder sb, String name, String val) {
        sb.append(name).append('=').append(val).append('\u0000');
    }
}
