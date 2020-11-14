package uk.org.webcompere.systemstubs.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@RunWith(Enclosed.class)
public class SecurityManagerRuleTest {
    public static class SetSecurityManagerAtConstruction {
        @Rule
        public SecurityManagerRule securityManagerRule = new SecurityManagerRule(mock(SecurityManager.class));

        @Test
        public void securityManagerAlreadyMocked() {
            // the security manager must be a mockito mock for this to work
            then(System.getSecurityManager())
                .should(never())
                .checkLink(any());
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class SetSecurityManagerOnTheFly {
        @Rule
        public SecurityManagerRule securityManagerRule = new SecurityManagerRule();

        @Mock
        public SecurityManager mockManager;

        @Test
        public void canMockTheSecurityManager() {
            // uses System.exit as an example of mocking the security manager
            // though the SystemExitRule does that better

            // the point is that a custom security manager can be provided somehow

            willThrow(new RuntimeException("don't go"))
                .given(mockManager)
                .checkExit(anyInt());

            securityManagerRule.setSecurityManager(mockManager);

            assertThatThrownBy(() -> System.exit(123))
                .hasMessage("don't go");

            then(mockManager).should().checkExit(123);
        }
    }
}
