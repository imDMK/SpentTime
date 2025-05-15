package com.github.imdmk.spenttime.util;

import dev.rollczi.litecommands.time.DurationParser;
import dev.rollczi.litecommands.time.TemporalAmountParser;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public final class DurationUtil {

    public static TemporalAmountParser<Duration> DATE_TIME_PARSER = new DurationParser()
            .withUnit("s", ChronoUnit.SECONDS)
            .withUnit("m", ChronoUnit.MINUTES)
            .withUnit("h", ChronoUnit.HOURS)
            .withUnit("d", ChronoUnit.DAYS)
            .withUnit("w", ChronoUnit.WEEKS)
            .withUnit("mo", ChronoUnit.MONTHS)
            .withUnit("y", ChronoUnit.YEARS);

    private DurationUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    public static String format(Duration duration) {
        if (duration.isZero() || duration.isNegative()) {
            return "<1s";
        }

        return DATE_TIME_PARSER.format(duration);
    }

    public static int toTicks(Duration duration) {
        return (int) (duration.toMillis() / 50L);
    }
}
