package com.github.imdmk.spenttime.user.repository;

import com.github.imdmk.spenttime.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Repository interface for managing and querying {@link User} data.
 * Supports both asynchronous and limited synchronous operations.
 */
public interface UserRepository {

    /**
     * Asynchronously finds a user by their unique UUID.
     *
     * @param uuid The UUID of the user.
     * @return A CompletableFuture containing an Optional with the user if found.
     */
    CompletableFuture<Optional<User>> findByUUID(@NotNull UUID uuid);

    /**
     * Asynchronously finds a user by their name.
     *
     * @param name The name (nickname) of the user.
     * @return A CompletableFuture containing an Optional with the user if found.
     */
    CompletableFuture<Optional<User>> findByName(@NotNull String name);

    /**
     * Synchronously finds a user by their name.
     * <p>
     * This method may be blocking, depending on the underlying implementation.
     * Use with caution, especially if using a remote or slow database.
     *
     * @param name The name (nickname) of the user.
     * @return An Optional containing the user if found.
     */
    Optional<User> findByNameDirect(@NotNull String name);

    /**
     * Asynchronously retrieves a list of users with the highest spent time.
     *
     * @param limit The maximum number of users to return.
     * @return A CompletableFuture with a list of top users, sorted by spent time descending.
     */
    CompletableFuture<List<User>> findTopUsersBySpentTime(long limit);

    /**
     * Asynchronously saves a user to the repository.
     * <p>
     * If the user already exists, their data will be updated.
     *
     * @param user The user to save.
     * @return A CompletableFuture containing the saved user.
     */
    CompletableFuture<User> save(@NotNull User user);

    /**
     * Asynchronously deletes a user from the repository.
     *
     * @param user The user to delete.
     * @return A CompletableFuture that completes when the operation finishes.
     */
    CompletableFuture<Void> delete(@NotNull User user);

    /**
     * Asynchronously resets the global spent time for all users.
     * <p>
     * This operation is potentially destructive and should be used with care.
     *
     * @return A CompletableFuture that completes when the reset is done.
     */
    CompletableFuture<Void> resetGlobalSpentTime();
}
