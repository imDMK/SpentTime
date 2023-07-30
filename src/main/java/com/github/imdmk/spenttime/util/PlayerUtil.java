package com.github.imdmk.spenttime.util;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class PlayerUtil {

    private PlayerUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    public static long getSpentTime(Player player) {
        long secondsPlayed = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20; //20 ticks = 1 second
        return TimeUnit.SECONDS.toMillis(secondsPlayed);
    }
}
