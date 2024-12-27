package com.github.imdmk.spenttime.user.repository.impl;

import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.repository.UserRepository;
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

    private final Dao<User, UUID> userDao;
    private final ExecutorService executor;

    private final UserCache userCache;

    public DaoUserRepositoryImpl(ConnectionSource connectionSource, UserCache userCache) throws SQLException {
        this.userDao = DaoManager.createDao(connectionSource, User.class);
        this.userCache = userCache;
        this.executor = Executors.newCachedThreadPool();

        TableUtils.createTableIfNotExists(connectionSource, User.class);
    }

    @Override
    public CompletableFuture<Optional<User>> findByUUID(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<User> userOptional = Optional.ofNullable(this.userDao.queryBuilder()
                        .where().eq("uuid", uuid)
                        .queryForFirst());

                userOptional.ifPresent(this.userCache::put);

                return userOptional;
            }
            catch (SQLException sqlException) {
                throw new CompletionException(sqlException);
            }
        }, this.executor).orTimeout(3L, TimeUnit.SECONDS);
    }

    @Override
    public Optional<User> findByName(String name) {
        try {
            Optional<User> userOptional = Optional.ofNullable(this.userDao.queryBuilder()
                    .where().eq("name", name)
                    .queryForFirst());

            userOptional.ifPresent(this.userCache::put);

            return userOptional;
        }
        catch (SQLException sqlException) {
            throw new CompletionException(sqlException);
        }
    }

    @Override
    public CompletableFuture<List<User>> findByOrderSpentTime(long limit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.userDao.queryBuilder()
                        .orderBy("spentTime", false)
                        .limit(limit)
                        .query();
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
                Dao.CreateOrUpdateStatus status = this.userDao.createOrUpdate(user);

                if (status.isCreated()) {
                    this.userCache.put(user);
                }

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
