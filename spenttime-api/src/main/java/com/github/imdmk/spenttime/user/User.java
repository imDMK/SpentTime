package com.github.imdmk.spenttime.user;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

public class User {

    private final UUID uuid;
    private String name;

    private long spentTime = 0L;

    private boolean needUpdate = false;

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public User(UUID uuid, String name, long spentTime) {
        this.uuid = uuid;
        this.name = name;
        this.spentTime = spentTime;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSpentTime() {
        return this.spentTime;
    }

    public Duration getSpentTimeAsDuration() {
        return Duration.ofMillis(this.spentTime);
    }

    public void setSpentTime(long spentTime) {
        this.spentTime = spentTime;
    }

    public void setSpentTime(Duration spentTime) {
        this.spentTime = spentTime.toMillis();
    }

    public boolean needUpdate() {
        return this.needUpdate;
    }

    public void setNeedUpdate(boolean needUpdate) {
        this.needUpdate = needUpdate;
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
