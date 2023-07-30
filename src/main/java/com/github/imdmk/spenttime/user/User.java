package com.github.imdmk.spenttime.user;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@DatabaseTable(tableName = "users")
public class User {

    @DatabaseField(columnName = "uuid", id = true)
    private UUID uuid;

    @DatabaseField(columnName = "name")
    private String name;

    @DatabaseField(columnName = "spentTime")
    private long spentTime;

    public User() {
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

    public Duration getDurationSpentTime() {
        return Duration.ofMillis(this.spentTime);
    }

    public void setSpentTime(long spentTime) {
        this.spentTime = spentTime;
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
        return this.spentTime == user.spentTime && Objects.equals(this.uuid, user.uuid) && Objects.equals(this.name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid, this.name, this.spentTime);
    }
}
