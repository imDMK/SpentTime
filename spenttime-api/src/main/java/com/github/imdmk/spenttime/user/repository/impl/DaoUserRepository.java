package com.github.imdmk.spenttime.user.repository.impl;

import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.user.repository.UserWrapper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DaoUserRepository implements UserRepository {

    private static final long ZERO_SPENT_TIME = 0L;

    private final Dao<UserWrapper, UUID> userDao;
    private final ExecutorService executor;

    private final UserCache userCache;

    public DaoUserRepository(ConnectionSource connectionSource, UserCache userCache) throws SQLException {
        Objects.requireNonNull(connectionSource, "connectionSource cannot be null");
        Objects.requireNonNull(userCache, "userCache cannot be null");

        this.userDao = DaoManager.createDao(connectionSource, UserWrapper.class);
        this.userCache = userCache;
        this.executor = Executors.newCachedThreadPool();

        TableUtils.createTableIfNotExists(connectionSource, UserWrapper.class);
    }

    @Override
    public CompletableFuture<Optional<User>> findByUUID(@NotNull UUID uuid) {
        Optional<User> cachedUser = this.userCache.getUserByUuid(uuid);
        if (cachedUser.isPresent()) {
            return CompletableFuture.completedFuture(cachedUser);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<User> userOptional = Optional.ofNullable(this.userDao.queryBuilder()
                        .where().eq("uuid", uuid)
                        .queryForFirst())
                        .map(UserWrapper::toUser);

                userOptional.ifPresent(this.userCache::cacheUser);

                return userOptional;
            }
            catch (SQLException sqlException) {
                throw new CompletionException(sqlException);
            }
        }, this.executor).orTimeout(3L, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Optional<User>> findByName(@NotNull String name) {
        Optional<User> cachedUser = this.userCache.getUserByName(name);
        if (cachedUser.isPresent()) {
            return CompletableFuture.completedFuture(cachedUser);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<User> userOptional = Optional.ofNullable(this.userDao.queryBuilder()
                        .where().eq("name", name)
                        .queryForFirst())
                        .map(UserWrapper::toUser);;

                userOptional.ifPresent(this.userCache::cacheUser);

                return userOptional;
            }
            catch (SQLException sqlException) {
                throw new CompletionException(sqlException);
            }
        }, this.executor).orTimeout(3L, TimeUnit.SECONDS);
    }

    @Override
    public Optional<User> findByNameDirect(@NotNull String name) {
        Optional<User> cachedUser = this.userCache.getUserByName(name);
        if (cachedUser.isPresent()) {
            return cachedUser;
        }

        try {
            Optional<User> userOptional = Optional.ofNullable(this.userDao.queryBuilder()
                .where().eq("name", name)
                .queryForFirst())
                .map(UserWrapper::toUser);

            userOptional.ifPresent(this.userCache::cacheUser);

            return userOptional;
        }
        catch (SQLException sqlException) {
            throw new CompletionException(sqlException);
        }
    }

    @Override
    public CompletableFuture<List<User>> findTopUsersBySpentTime(long limit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<User> users = this.userDao.queryBuilder()
                        .orderBy("spentTime", false)
                        .limit(limit).query()
                        .stream().map(UserWrapper::toUser)
                        .toList();

                users.forEach(this.userCache::cacheUser);
                return users;
            }
            catch (SQLException sqlException) {
                throw new CompletionException(sqlException);
            }
        }, this.executor).orTimeout(3L, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<User> save(@NotNull User user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UserWrapper wrapper = UserWrapper.from(user);

                this.userDao.createOrUpdate(wrapper);
                this.userCache.cacheUser(user);

                return user;
            }
            catch (SQLException sqlException) {
                throw new CompletionException(sqlException);
            }
        }, this.executor).orTimeout(3L, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Void> delete(@NotNull User user) {
        return CompletableFuture.runAsync(() -> {
            try {
                this.userDao.deleteById(user.getUuid());
                this.userCache.invalidateUser(user);
            }
            catch (SQLException sqlException) {
                throw new CompletionException(sqlException);
            }
        }, this.executor).orTimeout(3L, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Void> resetGlobalSpentTime() {
        return CompletableFuture.runAsync(() -> {
            try {
                this.userDao.updateBuilder()
                        .updateColumnValue("spentTime", ZERO_SPENT_TIME)
                        .update();

                this.userCache.forEachUser(user -> user.setSpentTime(ZERO_SPENT_TIME));
            }
            catch (SQLException sqlException) {
                throw new CompletionException(sqlException);
            }
        }, this.executor).orTimeout(3L, TimeUnit.SECONDS);
    }
}
