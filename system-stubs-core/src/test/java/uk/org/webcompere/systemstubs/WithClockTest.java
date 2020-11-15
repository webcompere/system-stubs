package uk.org.webcompere.systemstubs;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.systemstubs.SystemStubs.withClock;

class WithClockTest {
    @Test
    void when_clock_set_to_fixed_time_then_now_is_changed() throws Exception {
        LocalDateTime fixedTime = LocalDateTime.of(2020, 1, 2, 3, 4, 5, 6);
        withClock(fixedTime)
            .execute(() -> {
               assertThat(LocalDateTime.now()).isEqualTo(fixedTime);
            });
    }

    @Test
    void when_clock_set_to_fixed_time_then_now_is_same_for_each_call() throws Exception {
        LocalDateTime fixedTime = LocalDateTime.of(2020, 1, 2, 3, 4, 5, 6);
        withClock(fixedTime)
            .execute(() -> {
                assertThat(LocalDateTime.now()).isEqualTo(fixedTime);
                assertThat(LocalDateTime.now()).isEqualTo(fixedTime);
            });
    }

    @Test
    void when_clock_set_to_fixed_time_with_tick_then_now_is_different_for_each_call() throws Exception {
        LocalDateTime startTime = LocalDateTime.of(2020, 1, 2, 3, 4, 5, 6);
        LocalDateTime time2 = LocalDateTime.of(2020, 1, 2, 3, 5, 5, 6);
        LocalDateTime time3 = LocalDateTime.of(2020, 1, 2, 3, 6, 5, 6);
        withClock(startTime)
            .withTick(Duration.ofMinutes(1))
            .execute(() -> {
                assertThat(LocalDateTime.now()).isEqualTo(startTime);
                assertThat(LocalDateTime.now()).isEqualTo(time2);
                assertThat(LocalDateTime.now()).isEqualTo(time3);
            });
    }
}
