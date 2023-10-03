package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UserManager {

    private final UserRepository userRepository;

    private final Cache<UUID, User> uuidUserCache = CacheBuilder.newBuilder()
            .expireAfterAccess(12L, TimeUnit.HOURS)
            .build();
    private final Cache<String, User> nameUserCache = CacheBuilder.newBuilder()
            .expireAfterAccess(12L, TimeUnit.HOURS)
            .build();

    public UserManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(User user) {
        this.uuidUserCache.put(user.getUuid(), user);
        this.nameUserCache.put(user.getName(), user);
    }

    public User createUser(UUID uuid, String name) {
        User user = new User(uuid, name, 0L);

        this.addUser(user);
        this.userRepository.save(user);

        return user;
    }

    public User findOrCreateUser(UUID uuid, String name) {
        Optional<User> userOptional = this.getOrFindUser(uuid);

        return userOptional.orElseGet(() -> this.createUser(uuid, name));
    }

    public Optional<User> findUser(UUID uuid) {
        Optional<User> userOptional = this.userRepository.findByUUID(uuid);

        userOptional.ifPresent(this::addUser);

        return userOptional;
    }

    public Optional<User> findUser(String name) {
        Optional<User> userOptional = this.userRepository.findByName(name);

        userOptional.ifPresent(this::addUser);

        return userOptional;
    }

    public Optional<User> getOrFindUser(UUID uuid) {
        return this.getUser(uuid)
                .or(() -> this.findUser(uuid));
    }

    public Optional<User> getOrFindUser(String name) {
        return this.getUser(name)
                .or(() -> this.findUser(name));
    }

    public Optional<User> getUser(UUID uuid) {
        return Optional.ofNullable(this.uuidUserCache.asMap().get(uuid));
    }

    public Optional<User> getUser(String name) {
        return Optional.ofNullable(this.nameUserCache.asMap().get(name));
    }

    public Collection<String> getNameUserCache() {
        return Collections.unmodifiableCollection(this.nameUserCache.asMap().keySet());
    }
}
