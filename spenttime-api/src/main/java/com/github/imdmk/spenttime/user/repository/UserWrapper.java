package com.github.imdmk.spenttime.user.repository;

import com.github.imdmk.spenttime.user.User;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "users")
public class UserWrapper {

    @DatabaseField(columnName = "uuid", id = true, canBeNull = false)
    private UUID uuid;

    @DatabaseField(columnName = "name", canBeNull = false)
    private String name;

    @DatabaseField(columnName = "spentTime", canBeNull = false)
    private Long spentTime;

    public UserWrapper() {
    }

    public UserWrapper(UUID uuid, String name, Long spentTime) {
        this.uuid = uuid;
        this.name = name;
        this.spentTime = spentTime;
    }

    public static UserWrapper from(User user) {
        return new UserWrapper(user.getUuid(), user.getName(), user.getSpentTime());
    }

    public User toUser() {
        return new User(this.uuid, this.name, this.spentTime);
    }
}
