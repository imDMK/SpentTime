package com.github.imdmk.spenttime.user.repository.impl;

import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.user.repository.UserWrapper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DaoUserRepositoryImpl implements UserRepository {

    private final Dao<UserWrapper, UUID> userDao;
    private final ExecutorService executor;

    private final UserCache userCache;

    public DaoUserRepositoryImpl(ConnectionSource connectionSource, UserCache userCache) throws SQLException {
        this.userDao = DaoManager.createDao(connectionSource, UserWrapper.class);
        this.userCache = userCache;
        this.executor = Executors.newCachedThreadPool();

        TableUtils.createTableIfNotExists(connectionSource, UserWrapper.class);
    }

    @Override
    public CompletableFuture<Optional<User>> findByUUID(UUID uuid) {
        Optional<User> cachedUser = this.userCache.get(uuid);

        if (cachedUser.isPresent()) {
            return CompletableFuture.completedFuture(cachedUser);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<User> userOptional = Optional.ofNullable(this.userDao.queryBuilder()
                        .where().eq("uuid", uuid)
                        .queryForFirst())
                        .map(UserWrapper::toUser);

                userOptional.ifPresent(this.userCache::put);

                return userOptional;
            }
            catch (SQLException sqlException) {
                throw new CompletionException(sqlException);
            }
        }, this.executor).orTimeout(3L, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Optional<User>> findByName(String name) {
        Optional<User> cachedUser = this.userCache.get(name);

        if (cachedUser.isPresent()) {
            return CompletableFuture.completedFuture(cachedUser);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<User> userOptional = Optional.ofNullable(this.userDao.queryBuilder()
                        .where().eq("name", name)
                        .queryForFirst())
                        .map(UserWrapper::toUser);;

                userOptional.ifPresent(this.userCache::put);

                return userOptional;
            }
            catch (SQLException sqlException) {
                throw new CompletionException(sqlException);
            }
        }, this.executor).orTimeout(3L, TimeUnit.SECONDS);
    }

    @Override
    public Optional<User> findByNameDirect(String name) {
        Optional<User> cachedUser = this.userCache.get(name);

        if (cachedUser.isPresent()) {
            return cachedUser;
        }

        try {
            Optional<User> userOptional = Optional.ofNullable(this.userDao.queryBuilder()
                .where().eq("name", name)
                .queryForFirst())
                .map(UserWrapper::toUser);

            userOptional.ifPresent(this.userCache::put);

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
                return this.userDao.queryBuilder()
                        .orderBy("spentTime", false)
                        .limit(limit).query()
                        .stream().map(UserWrapper::toUser).toList();
            }
            catch (SQLException sqlException) {
                throw new CompletionException(sqlException);
            }
        }, this.executor).orTimeout(3L, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<User> save(User user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UserWrapper wrapper = UserWrapper.from(user);

                this.userDao.createOrUpdate(wrapper);
                this.userCache.put(user);

                return user;
            }
            catch (SQLException sqlException) {
                throw new CompletionException(sqlException);
            }
        }, this.executor).orTimeout(3L, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Void> delete(User user) {
        return CompletableFuture.runAsync(() -> {
            try {
                this.userDao.deleteById(user.getUuid());
                this.userCache.remove(user);
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
                        .updateColumnValue("spentTime", 0L)
                        .update();
            }
            catch (SQLException sqlException) {
                throw new CompletionException(sqlException);
            }
        }, this.executor).orTimeout(3L, TimeUnit.SECONDS);
    }
}
