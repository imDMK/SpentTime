package com.github.imdmk.spenttime.util;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.time.Duration;

public class PlayerUtil {

    private PlayerUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    public static Duration getSpentTime(OfflinePlayer player) {
        long secondsPlayed = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        return Duration.ofSeconds(secondsPlayed);
    }

    public static void setSpentTime(OfflinePlayer player, Duration time) {
        int ticksPlayed = (int) DurationUtil.toTicks(time);

        player.setStatistic(Statistic.PLAY_ONE_MINUTE, ticksPlayed);
    }
}
