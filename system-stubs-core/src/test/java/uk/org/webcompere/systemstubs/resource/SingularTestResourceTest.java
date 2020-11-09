package uk.org.webcompere.systemstubs.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class SingularTestResourceTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private SingularTestResource singularTestResource;

    @Test
    void oneCallToSetupDoesSetup() throws Exception {
        singularTestResource.setup();

        then(singularTestResource).should().doSetup();
    }

    @Test
    void twoCallsToSetupDoesOnlyOneSetup() throws Exception {
        singularTestResource.setup();
        singularTestResource.setup();

        then(singularTestResource).should().doSetup();
    }

    @Test
    void oneCallToTeardownDoesNothing() throws Exception {
        singularTestResource.teardown();

        then(singularTestResource).should(never()).doTeardown();
    }

    @Test
    void aTeardownHappensAfterASetup() throws Exception {
        singularTestResource.setup();
        singularTestResource.teardown();

        then(singularTestResource).should().doSetup();
        then(singularTestResource).should().doTeardown();
    }

    @Test
    void setupAndTearDownIsRepeatable() throws Exception {
        singularTestResource.setup();
        singularTestResource.teardown();

        then(singularTestResource).should().doSetup();
        then(singularTestResource).should().doTeardown();

        singularTestResource.setup();
        singularTestResource.teardown();

        then(singularTestResource).should(times(2)).doSetup();
        then(singularTestResource).should(times(2)).doTeardown();
    }

    @Test
    void aPreEmptiveTeardownShouldNotPreventASetupTeardown() throws Exception {
        singularTestResource.teardown();

        singularTestResource.setup();
        singularTestResource.teardown();

        then(singularTestResource).should().doSetup();
        then(singularTestResource).should().doTeardown();
    }
}
