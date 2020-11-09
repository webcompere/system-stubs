package uk.org.webcompere.systemstubs;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.lang.System.getSecurityManager;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(ReplaceUnderscores.class)
class WithSecurityManagerTest {
	private static final SecurityManager MANAGER = new SecurityManagerMock();

	@Test
	void specified_security_manager_is_present_while_statement_is_executed(
	) throws Exception {
		SystemStubs.withSecurityManager(
			MANAGER,
			() -> assertThat(getSecurityManager()).isSameAs(MANAGER)
		);
	}

    @Test
    void statement_is_executed() throws Exception {
        ThrowingRunnableMock statementMock = new ThrowingRunnableMock();

        SystemStubs.withSecurityManager(
			MANAGER,
			statementMock
		);

        assertThat(statementMock.hasBeenEvaluated).isTrue();
	}

	@Nested
	class the_security_manager_is_the_same_as_before {
		@Test
		void after_statement_is_executed(
		) throws Exception {
			SecurityManager originalManager = getSecurityManager();

			SystemStubs.withSecurityManager(
				MANAGER,
				() -> {
				}
			);

			assertThat(getSecurityManager()).isSameAs(originalManager);
		}

		@Test
		void after_statement_throws_exception() {
			SecurityManager originalSecurityManager = getSecurityManager();

			assertThatThrownBy(() ->
				SystemStubs.withSecurityManager(
					MANAGER,
					() -> {
						throw new RuntimeException();
					}
				)).isInstanceOf(RuntimeException.class);

			assertThat(getSecurityManager()).isSameAs(originalSecurityManager);
		}
	}
}
