package uk.org.webcompere.systemstubs;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.lang.System.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MuteSystemErrTest {

	@Test
	void no_text_is_written_to_System_err_by_statement(
	) throws Exception {
		ByteArrayOutputStream captureOutputStream = new ByteArrayOutputStream();
		setErr(new PrintStream(captureOutputStream));
		SystemStubs.muteSystemErr(
			() -> err.println("some text")
		);
		assertThat(captureOutputStream)
			.hasToString("");
	}

	@Nested
	class System_err_is_same_as_before
		extends RestoreSystemErrChecks
	{
		System_err_is_same_as_before() {
			super(SystemStubs::muteSystemErr);
		}
	}
}
