package com.github.imdmk.spenttime.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurationUtilTest {

    @Test
    void testHumanReadable() {
        Duration ofSeconds = Duration.ofSeconds(5);
        String secondsExcepted = "5s";
        String secondsResult = DurationUtil.toHumanReadable(ofSeconds);

        Duration ofMinutes = Duration.ofMinutes(5);
        String minutesExcepted = "5m";
        String minutesResult = DurationUtil.toHumanReadable(ofMinutes);

        Duration ofHours = Duration.ofHours(10);
        String hoursExcepted = "10h";
        String hoursResult = DurationUtil.toHumanReadable(ofHours);

        assertEquals(secondsExcepted, secondsResult);
        assertEquals(minutesExcepted, minutesResult);
        assertEquals(hoursExcepted, hoursResult);
    }

    @Test
    void testToTicks() {
        Duration ofSeconds = Duration.ofSeconds(6);
        long ofSecondsExcepted = 120;
        long ofSecondsResult = DurationUtil.toTicks(ofSeconds);

        Duration ofMinutes = Duration.ofMinutes(3);
        long ofMinutesExcepted = 3600;
        long ofMinutesResult = DurationUtil.toTicks(ofMinutes);

        assertEquals(ofSecondsExcepted, ofSecondsResult);
        assertEquals(ofMinutesExcepted, ofMinutesResult);
    }
}
