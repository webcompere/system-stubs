package uk.org.webcompere.systemstubs.internal;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class ProcessEnvironmentInterceptor {
    private static Map<String, String> CURRENT_ENVIRONMENT_VARIABLES = new HashMap<>();

    @SuppressFBWarnings("EI_EXPOSE_STATIC_REP2")
    public static void setEnv(Map<String, String> env) {
        CURRENT_ENVIRONMENT_VARIABLES = env;
    }

    /**
     * The equivalent of <code>getenv</code> in the original ProcessEnvironment, assuming that
     * mocking is "turned on"
     * @return the current effective environment
     */
    public static Map<String, String> getenv() {
        return filterNulls(CURRENT_ENVIRONMENT_VARIABLES);
    }

    public static String getenv(String name) {
        return getenv().get(name);
    }

    public static Map<String, String> environment() {
        return getenv();
    }

    /**
     * Ripped from the JDK implementation
     * @param m the map to convert
     * @return string representation
     */
    public static String toEnvironmentBlock(Map<String, String> m) {
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

    /**
     * Convert the requested environment variables to a Nix format
     * @param m the map of variables
     * @param envc the target array to receive the size
     * @return the byte array of environment variables
     */
    // code taken from the original in ProcessEnvironment
    @SuppressFBWarnings({"PZLA_PREFER_ZERO_LENGTH_ARRAYS", "DM_DEFAULT_ENCODING"})
    public static byte[] toEnvironmentBlock(Map<String, String> m, int[] envc) {
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
