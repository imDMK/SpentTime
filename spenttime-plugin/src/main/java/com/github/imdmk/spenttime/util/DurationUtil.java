package com.github.imdmk.spenttime.util;

import dev.rollczi.litecommands.time.DurationParser;
import dev.rollczi.litecommands.time.TemporalAmountParser;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for working with {@link Duration}, including formatting and conversion to ticks.
 * <p>
 * This class is not meant to be instantiated.
 */
public final class DurationUtil {

    /**
     * Shared parser for duration strings and formatting using LiteCommands' {@link DurationParser}.
     * Supports suffixes: s, m, h, d, w, mo, y
     */
    public static final TemporalAmountParser<Duration> DATE_TIME_PARSER = new DurationParser()
            .withUnit("s", ChronoUnit.SECONDS)
            .withUnit("m", ChronoUnit.MINUTES)
            .withUnit("h", ChronoUnit.HOURS)
            .withUnit("d", ChronoUnit.DAYS)
            .withUnit("w", ChronoUnit.WEEKS)
            .withUnit("mo", ChronoUnit.MONTHS)
            .withUnit("y", ChronoUnit.YEARS);

    /**
     * Private constructor to prevent instantiation.
     *
     * @throws UnsupportedOperationException always thrown when called
     */
    private DurationUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Formats the given duration into a human-readable string.
     *
     * @param duration the duration to format
     * @return a formatted string, or "&lt;1s" if the duration is zero or negative
     */
    public static String format(Duration duration) {
        if (duration.isZero() || duration.isNegative()) {
            return "<1s";
        }

        return DATE_TIME_PARSER.format(duration);
    }

    /**
     * Converts a duration to Minecraft ticks (1 tick = 50ms).
     *
     * @param duration the duration to convert
     * @return number of ticks representing the duration
     */
    public static int toTicks(Duration duration) {
        return (int) (duration.toMillis() / 50L);
    }
}
