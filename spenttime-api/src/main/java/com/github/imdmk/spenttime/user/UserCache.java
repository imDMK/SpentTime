package com.github.imdmk.spenttime.user;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class UserCache {

    private final Cache<UUID, User> uuidUserCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(12L))
            .expireAfterAccess(Duration.ofHours(2L))
            .build();

    private final Cache<String, User> nameUserCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(12L))
            .expireAfterAccess(Duration.ofHours(2L))
            .build();

    public void put(User user) {
        this.uuidUserCache.put(user.getUuid(), user);
        this.nameUserCache.put(user.getName(), user);
    }

    public void remove(User user) {
        this.uuidUserCache.invalidate(user.getUuid());
        this.nameUserCache.invalidate(user.getName());
    }

    public Optional<User> get(UUID uuid) {
        return Optional.ofNullable(this.uuidUserCache.asMap().get(uuid));
    }

    public Optional<User> get(String name) {
        return Optional.ofNullable(this.nameUserCache.asMap().get(name));
    }

    public Collection<String> getUserNames() {
        return Collections.unmodifiableCollection(this.nameUserCache.asMap().keySet());
    }
}
