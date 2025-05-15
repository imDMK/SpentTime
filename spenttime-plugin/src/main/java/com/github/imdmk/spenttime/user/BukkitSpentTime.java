package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.util.DurationUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class BukkitSpentTime {

    public static final int ZERO_SPENT_TIME = 0;

    private static final int TICKS_PER_SECOND = 20; // 20 ticks per second in Minecraft
    private static final Statistic SPENT_TIME = Statistic.PLAY_ONE_MINUTE;

    private final Server server;

    public BukkitSpentTime(@NotNull Server server) {
        this.server = Objects.requireNonNull(server, "server cannot be null");
    }

    public Duration getSpentTime(UUID uuid) {
        return this.getSpentTime(this.server.getOfflinePlayer(uuid));
    }

    public Duration getSpentTime(OfflinePlayer player) {
        int ticks = player.getStatistic(SPENT_TIME);
        return Duration.ofSeconds(ticks / TICKS_PER_SECOND);
    }

    public void resetSpentTime(UUID uuid) {
        this.resetSpentTime(this.server.getOfflinePlayer(uuid));
    }

    public void resetSpentTime(OfflinePlayer player) {
        player.setStatistic(SPENT_TIME, ZERO_SPENT_TIME);
    }

    public void resetSpentTime(Collection<OfflinePlayer> players) {
        players.forEach(this::resetSpentTime);
    }

    public void resetAllSpentTime() {
        this.resetSpentTime(this.offlinePlayers());
    }

    public void setSpentTime(UUID uuid, Duration duration) {
        this.setSpentTime(this.server.getOfflinePlayer(uuid), duration);
    }

    public void setSpentTime(OfflinePlayer player, Duration duration) {
        int ticks = DurationUtil.toTicks(duration);
        player.setStatistic(SPENT_TIME, ticks);
    }

    public <T> T withSpentTime(UUID uuid, Function<Duration, T> function) {
        return function.apply(this.getSpentTime(uuid));
    }

    public List<OfflinePlayer> offlinePlayers() {
        return Arrays.asList(this.server.getOfflinePlayers());
    }

}
