package com.github.imdmk.spenttime.user;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class UserCache {

    /**
     * Default expiration duration after write (12 hours).
     */
    private static final Duration EXPIRE_AFTER_WRITE = Duration.ofHours(12);

    /**
     * Default expiration duration after access (2 hours).
     */
    private static final Duration EXPIRE_AFTER_ACCESS = Duration.ofHours(2);

    private final Cache<UUID, User> cacheByUuid;
    private final Cache<String, User> cacheByName;

    /**
     * Constructs a new {@code UserCache} instance with specified expiration policies.
     *
     * @param expireAfterAccess duration after which entries expire if not accessed
     * @param expireAfterWrite  duration after which entries expire after write
     */
    public UserCache(@NotNull Duration expireAfterAccess, @NotNull Duration expireAfterWrite) {
        this.cacheByUuid = Caffeine.newBuilder()
                .expireAfterWrite(expireAfterWrite)
                .expireAfterAccess(expireAfterAccess)
                .build();

        this.cacheByName = Caffeine.newBuilder()
                .expireAfterWrite(expireAfterWrite)
                .expireAfterAccess(expireAfterAccess)
                .build();
    }

    /**
     * Constructs a new {@code UserCache} instance with default expiration policies.
     */
    public UserCache() {
        this(EXPIRE_AFTER_ACCESS, EXPIRE_AFTER_WRITE);
    }

    /**
     * Adds or updates a user in the cache.
     * Removes old username mapping if it exists, to keep name cache consistent.
     *
     * @param user the user to add or update; must not be null
     * @throws NullPointerException if user or any of its key properties are null
     */
    public void cacheUser(@NotNull User user) {
        Objects.requireNonNull(user, "user cannot be null");

        // Remove old name mapping(s) for this UUID to avoid stale cache entries
        this.cacheByName.asMap().entrySet().removeIf(entry ->
                entry.getValue().getUuid().equals(user.getUuid())
        );

        this.cacheByUuid.put(user.getUuid(), user);
        this.cacheByName.put(user.getName(), user);
    }

    /**
     * Removes the specified user from the cache, by UUID and name.
     *
     * @param user the user to remove; must not be null
     * @throws NullPointerException if user or any of its key properties are null
     */
    public void invalidateUser(@NotNull User user) {
        Objects.requireNonNull(user, "user cannot be null");

        this.cacheByUuid.invalidate(user.getUuid());
        this.cacheByName.invalidate(user.getName());
    }

    /**
     * Completely clears the cache of all users.
     */
    public void clearCache() {
        this.cacheByUuid.invalidateAll();
        this.cacheByName.invalidateAll();
    }

    /**
     * Retrieves a cached user by their UUID.
     *
     * @param uuid the UUID of the user; must not be null
     * @return an {@link Optional} containing the user if found, or empty if not present
     * @throws NullPointerException if uuid is null
     */
    @NotNull
    public Optional<User> getUserByUuid(@NotNull UUID uuid) {
        Objects.requireNonNull(uuid, "uuid cannot be null");
        return Optional.ofNullable(this.cacheByUuid.getIfPresent(uuid));
    }

    /**
     * Retrieves a cached user by their name.
     *
     * @param name the exact username; must not be null
     * @return an {@link Optional} containing the user if found, or empty if not present
     * @throws NullPointerException if name is null
     */
    @NotNull
    public Optional<User> getUserByName(@NotNull String name) {
        Objects.requireNonNull(name, "name cannot be null");
        return Optional.ofNullable(this.cacheByName.getIfPresent(name));
    }

    /**
     * Applies the given update action to every cached user.
     *
     * @param updateAction the action to apply to each cached user; must not be null
     * @throws NullPointerException if updateAction is null
     */
    public void forEachUser(@NotNull Consumer<User> updateAction) {
        Objects.requireNonNull(updateAction, "updateAction cannot be null");
        this.cacheByUuid.asMap().values().forEach(updateAction);
    }

    /**
     * Updates the cached username mapping after the user has changed their name.
     * Removes the old name mapping and adds the new one.
     *
     * @param user    the updated user; must not be null
     * @param oldName the old name to remove; must not be null
     * @throws NullPointerException if user or oldName is null
     */
    public void updateUserNameMapping(@NotNull User user, @NotNull String oldName) {
        Objects.requireNonNull(user, "user cannot be null");
        Objects.requireNonNull(oldName, "oldName cannot be null");

        this.cacheByName.invalidate(oldName);
        this.cacheByName.put(user.getName(), user);
    }

    /**
     * Returns an unmodifiable collection of all cached usernames.
     *
     * @return an unmodifiable collection of cached usernames
     */
    @NotNull
    public Collection<String> getAllCachedUserNames() {
        return Collections.unmodifiableCollection(this.cacheByName.asMap().keySet());
    }
}
