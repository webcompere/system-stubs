package uk.org.webcompere.systemstubs.rules;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Example to demonstrate how the @ClassRule integration works
 */
@RunWith(Suite.class)
// use this test suite to control the execution order of the child class tests, to prove no bleed
// in the environment between them
@Suite.SuiteClasses({ClassRuleTest.Test1.class, ClassRuleTest.Test2.class, ClassRuleTest.Test3.class})
public class ClassRuleTest {

    public static class Test1 {
        @ClassRule
        public static EnvironmentVariablesRule env1 = new EnvironmentVariablesRule("someVar", "value1");

        @Test
        public void test1() {
            assertThat(System.getenv("someVar")).isEqualTo("value1");
        }
    }

    public static class Test2 {

        @Test
        public void test2() {
            assertThat(System.getenv("someVar")).isNull();
        }
    }

    public static class Test3 {
        @ClassRule
        public static EnvironmentVariablesRule env1 = new EnvironmentVariablesRule("someVar", "value3");

        @Test
        public void test3() {
            assertThat(System.getenv("someVar")).isEqualTo("value3");
        }
    }
}
