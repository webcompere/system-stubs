package uk.org.webcompere.systemstubs.internal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProcessEnvironmentInterceptorTest {
    @AfterEach
    void resetEnvironment() {
        ProcessEnvironmentInterceptor.setEnv(new HashMap<>());
    }

    @Test
    void readingEnvironmentPreservesSourceAndItsIterators() {
        Map<String, String> source = new LinkedHashMap<>();
        source.put("unset", null);
        source.put("kept", "value");
        ProcessEnvironmentInterceptor.setEnv(source);
        Iterator<Map.Entry<String, String>> reader = source.entrySet().iterator();

        Map<String, String> filtered = ProcessEnvironmentInterceptor.getenv();

        assertEquals(Collections.singletonMap("kept", "value"), filtered);
        assertNull(ProcessEnvironmentInterceptor.getenv("unset"));
        Map<String, String> observed = new LinkedHashMap<>();
        reader.forEachRemaining(entry -> observed.put(entry.getKey(), entry.getValue()));
        assertTrue(observed.containsKey("unset"));
        assertNull(observed.get("unset"));
        assertEquals("value", observed.get("kept"));
        assertThrows(UnsupportedOperationException.class, () -> filtered.put("new", "value"));
    }

    @Test
    void unmodifiableEnvironmentCanBeRead() {
        Map<String, String> source = new HashMap<>();
        source.put("unset", null);
        source.put("kept", "value");
        ProcessEnvironmentInterceptor.setEnv(Collections.unmodifiableMap(source));

        assertEquals(Collections.singletonMap("kept", "value"), ProcessEnvironmentInterceptor.getenv());
    }
}
