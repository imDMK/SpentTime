package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import org.jetbrains.annotations.NotNull;

/**
 * Main API interface for accessing user-related components of the SpentTime module.
 * <p>
 * Provides access to:
 * - {@link UserCache}: in-memory user cache (by UUID and name)
 * - {@link UserRepository}: persistence layer for users
 */
public interface SpentTimeApi {

    /**
     * Returns the in-memory user cache.
     *
     * @return {@link UserCache} the user cache instance
     */
    @NotNull UserCache getUserCache();

    /**
     * Returns the user repository for data access operations.
     *
     * @return {@link UserRepository} the user repository
     */
    @NotNull UserRepository getUserRepository();
}
