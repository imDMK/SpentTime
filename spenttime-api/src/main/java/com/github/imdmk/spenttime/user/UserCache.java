package com.github.imdmk.spenttime.user;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class UserCache {

    private final Cache<UUID, User> usersByUuid = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(12L))
            .expireAfterAccess(Duration.ofHours(2L))
            .build();

    private final Cache<String, User> usersByName = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(12L))
            .expireAfterAccess(Duration.ofHours(2L))
            .build();

    public void put(User user) {
        this.usersByUuid.put(user.getUuid(), user);
        this.usersByName.put(user.getName(), user);
    }

    public void remove(User user) {
        this.usersByUuid.invalidate(user.getUuid());
        this.usersByName.invalidate(user.getName());
    }

    public void clear() {
        this.usersByUuid.invalidateAll();
        this.usersByName.invalidateAll();
    }

    public Optional<User> get(UUID uuid) {
        return Optional.ofNullable(this.usersByUuid.asMap().get(uuid));
    }

    public Optional<User> get(String name) {
        return Optional.ofNullable(this.usersByName.asMap().get(name));
    }

    public Collection<String> getUserNames() {
        return Collections.unmodifiableCollection(this.usersByName.asMap().keySet());
    }
}
