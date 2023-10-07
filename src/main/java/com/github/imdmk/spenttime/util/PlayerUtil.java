package com.github.imdmk.spenttime.util;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.time.Duration;

public class PlayerUtil {

    private PlayerUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    public static Duration getSpentTimeDuration(OfflinePlayer player) {
        long secondsPlayed = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        return Duration.ofSeconds(secondsPlayed);
    }

    public static long getSpentTime(OfflinePlayer offlinePlayer) {
        return getSpentTimeDuration(offlinePlayer).toMillis();
    }
}
