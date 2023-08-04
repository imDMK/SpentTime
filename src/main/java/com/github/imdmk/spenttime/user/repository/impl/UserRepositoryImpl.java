package com.github.imdmk.spenttime.user.repository.impl;

import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRepositoryImpl implements UserRepository {

    private final Logger logger;
    private final Dao<User, UUID> userDao;

    public UserRepositoryImpl(Logger logger, ConnectionSource connectionSource) throws SQLException {
        this.logger = logger;
        this.userDao = DaoManager.createDao(connectionSource, User.class);

        TableUtils.createTableIfNotExists(connectionSource, User.class);
    }

    @Override
    public Optional<User> findByUUID(UUID uuid) {
        try {
            return Optional.ofNullable(
                    this.userDao.queryBuilder()
                            .where().eq("uuid", uuid)
                            .queryForFirst()
            );
        }
        catch (SQLException sqlException) {
            this.logSevere("An error occurred while trying to query database.", sqlException);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByName(String name) {
        try {
            return Optional.ofNullable(
                    this.userDao.queryBuilder()
                            .where().eq("name", name)
                            .queryForFirst()
            );
        }
        catch (SQLException sqlException) {
            this.logSevere("An error occurred while trying to query database.", sqlException);
        }

        return Optional.empty();
    }

    @Override
    public List<User> findUsersByOrderSpentTime(long limit) {
        try {
            return this.userDao.queryBuilder()
                    .orderBy("spentTime", false)
                    .limit(limit)
                    .query();
        }
        catch (SQLException sqlException) {
            this.logSevere("An error occurred while trying to query database.", sqlException);
        }

        return Collections.emptyList();
    }

    @Override
    public void save(User user) {
        try {
            this.userDao.createOrUpdate(user);
        }
        catch (SQLException sqlException) {
            this.logSevere("An error occurred while trying to save user.", sqlException);
        }
    }

    @Override
    public void delete(UUID uuid) {
        try {
            this.userDao.deleteById(uuid);
        }
        catch (SQLException sqlException) {
            this.logSevere("An error occurred while trying to delete user from database.", sqlException);
        }
    }

    @Override
    public void createTable() {
        try {
            TableUtils.createTable(this.userDao.getConnectionSource(), User.class);
        }
        catch (SQLException sqlException) {
            this.logSevere("An error occurred while trying to drop table users from database.", sqlException);
        }
    }

    @Override
    public void dropTable() {
        try {
            TableUtils.dropTable(this.userDao.getConnectionSource(), User.class, false);
        }
        catch (SQLException sqlException) {
            this.logSevere("An error occurred while trying to drop table users from database.", sqlException);
        }
    }

    private void logSevere(String message, Throwable thrown) {
        this.logger.log(Level.SEVERE, message, thrown);
    }
}
