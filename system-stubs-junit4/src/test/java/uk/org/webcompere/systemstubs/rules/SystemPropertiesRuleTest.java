package uk.org.webcompere.systemstubs.rules;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.systemstubs.resource.PropertySource.fromResource;

@RunWith(Enclosed.class)
public class SystemPropertiesRuleTest {
    // order of tests is important to demonstrate no bleed between properties
    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class SystemPropertiesRuleNoBleedTest {

        @Rule
        public SystemPropertiesRule systemPropertiesRule = new SystemPropertiesRule("constructed", "yes");

        @Before
        public void before() {
            systemPropertiesRule.set("before", "true");
        }

        @Test
        public void a_canSetAPropertyAndReadIt() {
            System.setProperty("some", "property");
            assertThat(System.getProperty("some")).isEqualTo("property");
        }

        @Test
        public void b_propertyFromPreviousTestNotSet() {
            assertThat(System.getProperty("some")).isNull();
        }

        @Test
        public void c_canSetPropertyViaRuleObject() {
            systemPropertiesRule.set("some", "otherProperty");
            assertThat(System.getProperty("some")).isEqualTo("otherProperty");
        }

        @Test
        public void d_eachTestGetsFreshInstanceSoSetDoesNotApply() {
            assertThat(System.getProperty("some")).isNull();
        }

        @Test
        public void e_propertySetInBeforeMethodIsAvailable() {
            assertThat(System.getProperty("before")).isEqualTo("true");
        }

        @Test
        public void f_propertySetInConstructorIsAvailable() {
            assertThat(System.getProperty("constructed")).isEqualTo("yes");
        }
    }

    public static class WithPropertiesFromResources {
        @Rule
        public SystemPropertiesRule systemPropertiesRule = new SystemPropertiesRule()
            .set(fromResource("test.properties"));

        @Test
        public void g_withPropertiesFromFile() {
            assertThat(System.getProperty("value2")).isEqualTo("bar");
        }
    }
}
