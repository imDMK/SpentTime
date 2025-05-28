package com.github.imdmk.spenttime.infrastructure;

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

/**
 * Provides a high-level API for accessing and manipulating spent time
 * statistics of offline players using Bukkit's built-in statistics system.
 * <p>
 * Time is based on the {@link Statistic#PLAY_ONE_MINUTE} statistic, where
 * each unit equals one tick (1/20 of a second).
 * </p>
 */
public class BukkitSpentTime {

    /**
     * Constant representing zero spent time in ticks.
     */
    public static final int ZERO_SPENT_TIME = 0;

    private static final int TICKS_PER_SECOND = 20;
    private static final Statistic SPENT_TIME = Statistic.PLAY_ONE_MINUTE;

    private final Server server;

    /**
     * Creates a new instance of BukkitSpentTime using the given Bukkit server.
     *
     * @param server the Bukkit server instance
     * @throws NullPointerException if server is null
     */
    public BukkitSpentTime(@NotNull Server server) {
        this.server = Objects.requireNonNull(server, "server cannot be null");
    }

    /**
     * Gets the spent time of an offline player by their UUID.
     *
     * @param uuid the UUID of the player
     * @return the time spent on the server
     */
    public Duration getSpentTime(@NotNull UUID uuid) {
        return this.getSpentTime(this.server.getOfflinePlayer(uuid));
    }

    /**
     * Gets the spent time of the specified offline player.
     *
     * @param player the offline player
     * @return the time spent on the server
     */
    public Duration getSpentTime(@NotNull OfflinePlayer player) {
        int ticks = player.getStatistic(SPENT_TIME);
        return Duration.ofSeconds(ticks / TICKS_PER_SECOND);
    }

    /**
     * Resets the spent time of an offline player identified by UUID.
     *
     * @param uuid the UUID of the player
     */
    public void resetSpentTime(@NotNull UUID uuid) {
        this.resetSpentTime(this.server.getOfflinePlayer(uuid));
    }

    /**
     * Resets the spent time of the specified offline player.
     *
     * @param player the offline player
     */
    public void resetSpentTime(@NotNull OfflinePlayer player) {
        player.setStatistic(SPENT_TIME, ZERO_SPENT_TIME);
    }

    /**
     * Resets the spent time for all provided offline players.
     *
     * @param players the collection of players
     */
    public void resetSpentTime(@NotNull Collection<OfflinePlayer> players) {
        players.forEach(this::resetSpentTime);
    }

    /**
     * Resets the spent time for all offline players known to the server.
     */
    public void resetAllSpentTime() {
        this.resetSpentTime(this.offlinePlayers());
    }

    /**
     * Sets the spent time for an offline player identified by UUID.
     *
     * @param uuid     the UUID of the player
     * @param duration the duration to set
     */
    public void setSpentTime(@NotNull UUID uuid, @NotNull Duration duration) {
        this.setSpentTime(this.server.getOfflinePlayer(uuid), duration);
    }

    /**
     * Sets the spent time for the specified offline player.
     *
     * @param player   the offline player
     * @param duration the duration to set
     */
    public void setSpentTime(@NotNull OfflinePlayer player, @NotNull Duration duration) {
        int ticks = DurationUtil.toTicks(duration);
        player.setStatistic(SPENT_TIME, ticks);
    }

    /**
     * Applies a function to the spent time of a player identified by UUID.
     *
     * @param uuid     the UUID of the player
     * @param function a function to apply to the player's spent time
     * @param <T>      the return type
     * @return the result of the function applied to the player's spent time
     */
    public <T> T withSpentTime(@NotNull UUID uuid, @NotNull Function<Duration, T> function) {
        return function.apply(this.getSpentTime(uuid));
    }

    /**
     * Returns a list of all offline players known to the server.
     *
     * @return the list of offline players
     */
    public @NotNull List<OfflinePlayer> offlinePlayers() {
        return Arrays.asList(this.server.getOfflinePlayers());
    }

}
