package com.github.imdmk.spenttime.util;

import java.time.Duration;

public class DurationUtil {

    private DurationUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    public static String toHumanReadable(Duration duration) {
        Duration ofSeconds = Duration.ofSeconds(duration.toSeconds());

        if (ofSeconds.isZero() || ofSeconds.isNegative()) {
            return "<1s";
        }

        return ofSeconds.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

    public static long toTicks(Duration duration) {
        return duration.toMillis() / 50L;
    }
}
