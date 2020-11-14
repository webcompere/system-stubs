package uk.org.webcompere.systemstubs.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static uk.org.webcompere.systemstubs.resource.Resources.execute;
import static uk.org.webcompere.systemstubs.resource.Resources.with;

@ExtendWith(MockitoExtension.class)
class ResourcesTest {
    @Mock
    private TestResource firstResource;

    @Mock
    private TestResource secondResource;

    @Mock
    private TestResource thirdResource;

    @Mock
    private Callable<String> callable;

    @Test
    void canRunRunnableWithNoResources() throws Exception {
        execute(callable);

        then(callable).should().call();
    }

    @Test
    void canRunRunnableWithOneResource() throws Exception {
        execute(callable, firstResource);

        then(callable).should().call();
        then(firstResource).should().setup();
        then(firstResource).should().teardown();
    }

    @Test
    void runnablesReturnIsReturned() throws Exception {
        given(callable.call()).willReturn("bar");

        String result = execute(callable, firstResource);
        assertThat(result).isEqualTo("bar");
    }

    @Test
    void canRunRunnableWithTwoResources() throws Exception {
        execute(callable, firstResource, secondResource);

        then(callable).should().call();
        then(firstResource).should().setup();
        then(firstResource).should().teardown();

        then(secondResource).should().setup();
        then(secondResource).should().teardown();
    }

    @Test
    void canRunRunnableWithThreeResources() throws Exception {
        execute(callable, firstResource, secondResource, thirdResource);

        then(callable).should().call();
        then(firstResource).should().setup();
        then(firstResource).should().teardown();

        then(secondResource).should().setup();
        then(secondResource).should().teardown();

        then(thirdResource).should().setup();
        then(thirdResource).should().teardown();
    }

    @Test
    void canRunRunnableWithThreeResourcesUsingWith() throws Exception {
        with(firstResource, secondResource, thirdResource)
            .execute(callable);

        then(callable).should().call();
        then(firstResource).should().setup();
        then(firstResource).should().teardown();

        then(secondResource).should().setup();
        then(secondResource).should().teardown();

        then(thirdResource).should().setup();
        then(thirdResource).should().teardown();
    }

    @Test
    void whenSecondResourceFailsToStartThirdIsNeverTouchedButSecondIsCleaned() throws Exception {
        willThrow(new RuntimeException("boom")).given(secondResource).setup();

        assertThatThrownBy(() -> execute(callable, firstResource, secondResource, thirdResource))
            .hasMessage("boom");

        then(callable).should(never()).call();
        then(firstResource).should().setup();
        then(firstResource).should().teardown();

        then(secondResource).should().setup();
        then(secondResource).should().teardown();

        then(thirdResource).should(never()).setup();
        then(thirdResource).should(never()).teardown();
    }

    @Test
    void whenSecondResourceFailsToCleanThenThirdIsStillCleaned() throws Exception {
        willThrow(new RuntimeException("boom")).given(secondResource).teardown();

        assertThatThrownBy(() -> execute(callable, firstResource, secondResource, thirdResource))
            .hasMessage("boom");

        then(callable).should().call();
        then(firstResource).should().setup();
        then(firstResource).should().teardown();

        then(secondResource).should().setup();
        then(secondResource).should().teardown();

        then(thirdResource).should().setup();
        then(thirdResource).should().teardown();
    }

    @Test
    void whenRunnableFailsTheErrorIsFromTheRunnable() throws Exception {
        willThrow(new RuntimeException("boom")).given(callable).call();

        assertThatThrownBy(() -> execute(callable, firstResource, secondResource, thirdResource))
            .hasMessage("boom");

        then(callable).should().call();
        then(firstResource).should().setup();
        then(firstResource).should().teardown();

        then(secondResource).should().setup();
        then(secondResource).should().teardown();

        then(thirdResource).should().setup();
        then(thirdResource).should().teardown();
    }
}
