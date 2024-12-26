package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.util.DurationUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Statistic;

import java.time.Duration;
import java.util.UUID;

public class BukkitPlayerSpentTimeService {

    private final Server server;

    public BukkitPlayerSpentTimeService(Server server) {
        this.server = server;
    }

    public Duration getSpentTime(OfflinePlayer player) {
        long secondsPlayed = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        return Duration.ofSeconds(secondsPlayed);
    }

    public void resetSpentTime(OfflinePlayer offlinePlayer) {
        offlinePlayer.setStatistic(Statistic.PLAY_ONE_MINUTE, 0);
    }

    public void resetSpentTime(UUID playerUuid) {
        this.server.getOfflinePlayer(playerUuid).setStatistic(Statistic.PLAY_ONE_MINUTE, 0);
    }

    public void setSpentTime(UUID uuid, Duration time) {
        this.server.getOfflinePlayer(uuid).setStatistic(Statistic.PLAY_ONE_MINUTE, DurationUtil.toTicks(time));
    }
}
