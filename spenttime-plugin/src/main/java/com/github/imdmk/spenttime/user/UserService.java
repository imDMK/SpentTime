package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.user.repository.UserRepository;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {

    private final Logger logger;
    private final UserRepository userRepository;
    private final UserCache userCache;
    private final BukkitSpentTime bukkitSpentTime;

    public UserService(
            @NotNull Logger logger,
            @NotNull UserRepository userRepository,
            @NotNull UserCache userCache,
            @NotNull BukkitSpentTime bukkitSpentTime
    ) {
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository cannot be null");
        this.userCache = Objects.requireNonNull(userCache, "userCache cannot be null");
        this.bukkitSpentTime = Objects.requireNonNull(bukkitSpentTime, "bukkitSpentTime cannot be null");
    }

    public CompletableFuture<User> findOrCreateUser(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();

        return this.userRepository.findByUUID(uuid)
                .thenCompose(optionalUser -> optionalUser
                        .map(CompletableFuture::completedFuture)
                        .orElseGet(() -> this.saveUser(new User(uuid, name)))
                )
                .exceptionally(throwable -> {
                    this.logger.log(Level.SEVERE, "Failed to load user: " + name, throwable);
                    return null;
                });
    }

    public void setSpentTime(@NotNull User user, Duration duration) {
        user.setSpentTime(duration);
        this.bukkitSpentTime.setSpentTime(user.getUuid(), duration);
        this.saveUser(user)
                .exceptionally(throwable -> {
                    this.logger.log(Level.SEVERE, "An error occurred while setting spent time: " + user, throwable);
                    return null;
                });
    }

    public boolean updateUser(@NotNull Player player, @NotNull User user) {
        return this.updateUserData(player, user, this::saveUser);
    }

    /**
     * Updates the user object based on player data. Returns true if any data changed.
     */
    public boolean updateUserData(@NotNull Player player, @NotNull User user, @Nullable Consumer<User> ifUpdated) {
        boolean updated = false;

        String newName = player.getName();
        String oldName = user.getName();
        if (!newName.equals(oldName)) {
            user.setName(newName);
            this.userCache.updateUserNameMapping(user, oldName);
            updated = true;
        }

        Duration spentTime = this.bukkitSpentTime.getSpentTime(player);
        if (!spentTime.equals(user.getSpentTimeAsDuration())) {
            user.setSpentTime(spentTime);
            updated = true;
        }

        if (updated && ifUpdated != null) {
            ifUpdated.accept(user);
        }

        return updated;
    }

    public CompletableFuture<User> saveUser(@NotNull User user) {
        return this.userRepository.save(user)
                .exceptionally(throwable -> {
                    this.logger.log(Level.SEVERE, "Failed to save user '" + user.getName() + "'", throwable);
                    throw new RuntimeException(throwable);
                });
    }
}
