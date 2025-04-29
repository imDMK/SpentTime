package com.github.imdmk.spenttime.database;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseService {

    private final Logger logger;
    private final File dataFolder;
    private final DatabaseConfiguration databaseConfiguration;

    private HikariDataSource dataSource;
    private ConnectionSource connectionSource;

    public DatabaseService(Logger logger, File dataFolder, DatabaseConfiguration databaseConfiguration) {
        this.logger = logger;
        this.dataFolder = dataFolder;
        this.databaseConfiguration = databaseConfiguration;
    }

    public void connect() throws SQLException, IllegalStateException {
        if (this.dataSource != null || this.connectionSource != null) {
            throw new IllegalStateException("DatabaseService is already connected.");
        }

        if (!this.dataFolder.exists() && !this.dataFolder.mkdirs()) {
            throw new IllegalStateException("Unable to create data folder: " + this.dataFolder.getAbsolutePath());
        }

        this.dataSource = this.createHikariDataSource();
        DatabaseMode databaseMode = this.databaseConfiguration.databaseMode;

        switch (databaseMode) {
            case SQLITE -> {
                this.dataSource.setDriverClassName("org.sqlite.JDBC");
                this.dataSource.setJdbcUrl("jdbc:sqlite:" + this.dataFolder + "/database.db");
            }

            case MYSQL -> {
                this.dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                this.dataSource.setJdbcUrl("jdbc:mysql://" + this.databaseConfiguration.hostname + ":" + this.databaseConfiguration.port + "/" + this.databaseConfiguration.database);
            }

            default -> throw new IllegalStateException("Unknown database mode: " + databaseMode.name());
        }

        try {
            this.connectionSource = new DataSourceConnectionSource(this.dataSource, this.dataSource.getJdbcUrl());

            this.logger.info("Connected to " + databaseMode.name() + " database.");
        }
        catch (SQLException sqlException) {
            this.logger.log(Level.SEVERE, "Failed to connect to database", sqlException);
            this.dataSource.close(); // We're closing after exception, otherwise can cause a leak
            this.dataSource = null;
            throw sqlException;
        }
    }

    private HikariDataSource createHikariDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setMaximumPoolSize(5);
        dataSource.setUsername(this.databaseConfiguration.username);
        dataSource.setPassword(this.databaseConfiguration.password);
        dataSource.addDataSourceProperty("cachePrepStmts", true);
        dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        dataSource.addDataSourceProperty("useServerPrepStmts", true);
        return dataSource;
    }

    public void close() {
        if (this.dataSource == null || this.connectionSource == null) {
            this.logger.warning("DatabaseService#close() called, but service was not connected.");
            return;
        }

        try {
            this.connectionSource.close();
        }
        catch (Exception e) {
            this.logger.log(Level.SEVERE, "Failed to close ConnectionSource", e);
        }

        try {
            this.dataSource.close();
        }
        catch (Exception e) {
            this.logger.log(Level.SEVERE, "Failed to close DataSource", e);
        }

        this.connectionSource = null;
        this.dataSource = null;

        this.logger.info("Database connection closed successfully.");
    }

    public ConnectionSource getConnectionSource() {
        return this.connectionSource;
    }
}
