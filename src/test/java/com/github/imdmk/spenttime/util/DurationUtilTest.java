package com.github.imdmk.spenttime.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurationUtilTest {

    @Test
    void testHumanReadable() {
        Duration negative = Duration.ZERO.minusDays(5);
        String negativeExcepted = "<1s";
        String negativeResult = DurationUtil.toHumanReadable(negative);

        Duration ofSeconds = Duration.ofSeconds(5);
        String secondsExcepted = "5s";
        String secondsResult = DurationUtil.toHumanReadable(ofSeconds);

        Duration ofMinutes = Duration.ofMinutes(5);
        String minutesExcepted = "5m";
        String minutesResult = DurationUtil.toHumanReadable(ofMinutes);

        Duration ofHours = Duration.ofHours(10);
        String hoursExcepted = "10h";
        String hoursResult = DurationUtil.toHumanReadable(ofHours);

        Duration ofDays = Duration.ofDays(5);
        String daysExcepted = "5d";
        String daysResult = DurationUtil.toHumanReadable(ofDays);

        Duration ofWeeks = Duration.ofDays(14);
        String weeksExcepted = "2w";
        String weeksResult = DurationUtil.toHumanReadable(ofWeeks);

        Duration ofMonths = Duration.ofDays(60);
        String monthsExcepted = "2mo";
        String monthsResult = DurationUtil.toHumanReadable(ofMonths);

        assertEquals(negativeExcepted, negativeResult);
        assertEquals(secondsExcepted, secondsResult);
        assertEquals(minutesExcepted, minutesResult);
        assertEquals(hoursExcepted, hoursResult);
        assertEquals(daysExcepted, daysResult);
        assertEquals(weeksExcepted, weeksResult);
        assertEquals(monthsExcepted, monthsResult);
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
