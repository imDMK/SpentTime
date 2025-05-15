package com.github.imdmk.spenttime.user;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

public class User {

    private final UUID uuid;
    private String name;

    private long spentTime = 0L;

    public User(@NotNull UUID uuid, @NotNull String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public User(@NotNull UUID uuid, @NotNull String name, long spentTime) {
        this.uuid = uuid;
        this.name = name;
        this.spentTime = spentTime;
    }

    public @NotNull UUID getUuid() {
        return this.uuid;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public long getSpentTime() {
        return this.spentTime;
    }

    public @NotNull Duration getSpentTimeAsDuration() {
        return Duration.ofMillis(this.spentTime);
    }

    public void setSpentTime(long spentTime) {
        this.spentTime = spentTime;
    }

    public void setSpentTime(@NotNull Duration spentTime) {
        this.spentTime = spentTime.toMillis();
    }

    @Override
    public String toString() {
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

        if (!(o instanceof User user)) {
            return false;
        }

        return Objects.equals(this.uuid, user.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }
}
