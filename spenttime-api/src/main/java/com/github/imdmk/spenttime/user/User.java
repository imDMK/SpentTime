package com.github.imdmk.spenttime.user;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player who has spent time on the server.
 * <p>
 * Holds the player's UUID, current name, and total time spent on the server
 * in milliseconds.
 * Supports conversions to {@link Duration}.
 */
public class User {

    private final UUID uuid;
    private String name;

    private long spentTime = 0L;

    /**
     * Creates a new User instance with the specified UUID and name.
     *
     * @param uuid the unique UUID of the player; must not be null
     * @param name the current name of the player; must not be null
     */
    public User(@NotNull UUID uuid, @NotNull String name) {
        this.uuid = Objects.requireNonNull(uuid, "uuid cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
    }

    /**
     * Creates a new User instance with the specified UUID, name, and spent time.
     *
     * @param uuid      the unique UUID of the player; must not be null
     * @param name      the current name of the player; must not be null
     * @param spentTime total time spent on the server in milliseconds; must be >= 0
     * @throws IllegalArgumentException if spentTime is negative
     */
    public User(@NotNull UUID uuid, @NotNull String name, long spentTime) {
        this(uuid, name);
        this.setSpentTime(spentTime);
    }

    /**
     * Returns the UUID of the player.
     *
     * @return the non-null UUID
     */
    public @NotNull UUID getUuid() {
        return this.uuid;
    }

    /**
     * Returns the current name of the player.
     *
     * @return the non-null player name
     */
    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Updates the player's name.
     *
     * @param name the new name; must not be null
     */
    public void setName(@NotNull String name) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
    }

    /**
     * Returns the total time the player has spent on the server in milliseconds.
     *
     * @return total spent time in milliseconds, never negative
     */
    public long getSpentTime() {
        return this.spentTime;
    }

    /**
     * Returns the total time spent on the server as a {@link Duration}.
     *
     * @return a non-null Duration representing the spent time
     */
    public @NotNull Duration getSpentTimeAsDuration() {
        return Duration.ofMillis(this.spentTime);
    }

    /**
     * Sets the total spent time in milliseconds.
     *
     * @param spentTime the total time spent must be >= 0
     * @throws IllegalArgumentException if spentTime is negative
     */
    public void setSpentTime(long spentTime) {
        if (spentTime < 0) {
            throw new IllegalArgumentException("Spent time cannot be negative");
        }
        this.spentTime = spentTime;
    }

    /**
     * Sets the total spent time using a {@link Duration}.
     *
     * @param spentTime Duration representing the total spent time; must not be null
     * @throws IllegalArgumentException if the duration is negative
     */
    public void setSpentTime(@NotNull Duration spentTime) {
        Objects.requireNonNull(spentTime, "spentTime cannot be null");
        this.setSpentTime(spentTime.toMillis());
    }

    @Override
    public @NotNull String toString() {
        return "User{" +
                "uuid=" + this.uuid +
                ", name='" + this.name + '\'' +
                ", spentTime=" + this.spentTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return this.uuid.equals(user.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }
}
