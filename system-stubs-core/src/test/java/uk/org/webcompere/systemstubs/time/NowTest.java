package uk.org.webcompere.systemstubs.time;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static uk.org.webcompere.systemstubs.resource.Resources.with;

class NowTest {

    @Test
    void defaultMockingDoesNothing() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        with(new Now())
            .execute(() -> {
               LocalDateTime then = LocalDateTime.now();
               assertThat(then).isCloseTo(now, within(5, SECONDS));
            });
    }

    @Test
    void realTimeIsCloseToRealTimeByDefault() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Now nowMock = new Now();
        with(nowMock)
            .execute(() -> {
                assertThat(nowMock.getRealTime())
                    .isCloseTo(now.toInstant(ZoneId.systemDefault().getRules().getOffset(now)),
                        within(5, SECONDS));
            });
    }

    @Test
    void canMakeFixedTime() throws Exception {
        Now nowMock = new Now();
        nowMock.fixed(LocalDateTime.of(2021, 3, 12, 9, 56));
        with(nowMock)
            .execute(() -> {
                assertThat(LocalDateTime.now())
                    .isEqualTo(LocalDateTime.of(2021, 3, 12, 9, 56));
            });
    }
}
