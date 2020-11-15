package uk.org.webcompere.systemstubs.time;

import org.mockito.Answers;
import org.mockito.MockedStatic;
import uk.org.webcompere.systemstubs.resource.SingularTestResource;

import java.time.*;
import java.util.function.Function;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockStatic;

public class ClockStub extends SingularTestResource {
    private Instant currentTime;
    private Function<Instant, Instant> ticker = Function.identity();
    private MockedStatic<Instant> mockInstant;

    public ClockStub() {
        this(Instant.now());
    }

    public ClockStub(LocalDateTime localDateTime) {
        this(localDateTime.toInstant(ZoneOffset.UTC));
    }

    public ClockStub(ZonedDateTime zonedDateTime) {
        this(zonedDateTime.toInstant());
    }


    public ClockStub(Instant currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    protected void doSetup() throws Exception {
        Instant i = Instant.ofEpochMilli(123);
        mockInstant = mockStatic(Instant.class);
        mockInstant.when(() -> Instant.now()).thenAnswer(o -> getMockTime());
        //i -> getMockTime());
    }

    @Override
    protected void doTeardown() throws Exception {
        mockInstant.close();
    }

    public ClockStub withTick(Duration tickDuration) {
        ticker = instant -> instant.plus(tickDuration);
        return this;
    }

    public Instant getMockTime() {
        Instant response = currentTime;
        currentTime = ticker.apply(currentTime);
        return response;
    }


}
