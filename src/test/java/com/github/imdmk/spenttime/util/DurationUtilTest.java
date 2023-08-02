package com.github.imdmk.spenttime.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurationUtilTest {

    @Test
    void testSeconds() {
        Duration ofSeconds = Duration.ofSeconds(5);

        String excepted = "5s";
        String result = DurationUtil.toHumanReadable(ofSeconds);

        assertEquals(excepted, result);
    }

    @Test
    void testMinutes() {
        Duration ofMinutes = Duration.ofMinutes(5);

        String excepted = "5m";
        String result = DurationUtil.toHumanReadable(ofMinutes);

        assertEquals(excepted, result);
    }

    @Test
    void testHours() {
        Duration ofHours = Duration.ofHours(10);

        String excepted = "10h";
        String result = DurationUtil.toHumanReadable(ofHours);

        assertEquals(excepted, result);
    }

    @Test
    void testDays() {
        Duration ofDays = Duration.ofDays(30);

        String excepted = "720h";
        String result = DurationUtil.toHumanReadable(ofDays);

        assertEquals(excepted, result);
    }
}
