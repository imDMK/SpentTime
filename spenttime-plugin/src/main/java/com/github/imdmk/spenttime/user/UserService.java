package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.infrastructure.BukkitSpentTime;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service layer responsible for managing user data,
 * including synchronization with Bukkit statistics and repository persistence.
 * <p>
 * Provides high-level operations to find, update, create and store {@link User} entities.
 * </p>
 */
public class UserService {

    private final Logger logger;
    private final UserRepository userRepository;
    private final UserCache userCache;
    private final BukkitSpentTime bukkitSpentTime;

    /**
     * Constructs the UserService with required dependencies.
     *
     * @param logger           the logger for error reporting
     * @param userRepository   the repository for persistent user storage
     * @param userCache        the in-memory user cache
     * @param bukkitSpentTime  the API for reading/writing Bukkit spent time stats
     */
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

    /**
     * Retrieves a user by UUID or creates a new one if not present in the repository.
     *
     * @param uuid the UUID of the player
     * @param name the name of the player
     * @return a future resolving to the existing or newly created user
     */
    public CompletableFuture<User> findOrCreateUser(@NotNull UUID uuid, @NotNull String name) {
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

    /**
     * Updates the spent time of a user and synchronizes it with Bukkit statistics.
     *
     * @param user     the user to update
     * @param duration the new spent time
     */
    public void setSpentTime(@NotNull User user, @NotNull Duration duration) {
        user.setSpentTime(duration);
        this.bukkitSpentTime.setSpentTime(user.getUuid(), duration);

        this.saveUser(user)
                .exceptionally(throwable -> {
                    this.logger.log(Level.SEVERE, "An error occurred while setting spent time: " + user, throwable);
                    return null;
                });
    }

    /**
     * Updates user data based on the given player and saves it if modified.
     *
     * @param player the player providing current data
     * @param user   the user to update
     * @return true if user data was changed
     */
    public boolean updateUser(@NotNull OfflinePlayer player, @NotNull User user) {
        return this.updateUserData(player, user, this::saveUser);
    }

    /**
     * Updates user fields based on the current player state (name, spent time).
     *
     * @param player     the Bukkit player
     * @param user       the user to update
     * @param ifUpdated  optional callback if the user was modified
     * @return true if the user was modified
     */
    public boolean updateUserData(
            @NotNull OfflinePlayer player,
            @NotNull User user,
            @Nullable Consumer<User> ifUpdated
    ) {
        boolean updated = false;

        String newName = player.getName();
        String oldName = user.getName();

        if (newName != null && !newName.equals(oldName)) {
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

    /**
     * Persists a user to the repository.
     *
     * @param user the user to save
     * @return a future resolving to the saved user
     * @throws RuntimeException if an error occurs during saving
     */
    public CompletableFuture<User> saveUser(@NotNull User user) {
        return this.userRepository.save(user)
                .exceptionally(throwable -> {
                    this.logger.log(Level.SEVERE, "Failed to save user '" + user.getName() + "'", throwable);
                    throw new RuntimeException(throwable);
                });
    }
}
