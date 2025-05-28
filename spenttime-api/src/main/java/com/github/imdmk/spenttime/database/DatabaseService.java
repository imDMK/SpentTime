package com.github.imdmk.spenttime.database;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the database connection lifecycle using HikariCP connection pool and ORMLite's ConnectionSource.
 * Supports multiple database modes (SQLite, MySQL) based on configuration.
 * <p>
 * Responsible for establishing and closing connections, including configuration of connection parameters.
 */
public class DatabaseService {

    private final Logger logger;
    private final File dataFolder;
    private final DatabaseConfiguration databaseConfiguration;

    private HikariDataSource dataSource;
    private ConnectionSource connectionSource;

    /**
     * Constructs a new {@code DatabaseService} with the given logger, data folder and database configuration.
     *
     * @param logger                the logger instance to log info and errors; must not be null
     * @param dataFolder            the folder for local data storage (used by SQLite); must not be null
     * @param databaseConfiguration the configuration containing database mode and credentials; must not be null
     */
    public DatabaseService(
            @NotNull Logger logger,
            @NotNull File dataFolder,
            @NotNull DatabaseConfiguration databaseConfiguration
    ) {
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.dataFolder = Objects.requireNonNull(dataFolder, "dataFolder cannot be null");
        this.databaseConfiguration = Objects.requireNonNull(databaseConfiguration, "databaseConfiguration cannot be null");
    }

    /**
     * Establishes the database connection and initializes the connection pool and ORMLite's {@link ConnectionSource}.
     * <p>
     * Depending on the configured {@link DatabaseMode}, sets up connection parameters for SQLite or MySQL.
     * This method must only be called once before using the service.
     *
     * @throws SQLException           if a database access error occurs during initialization
     * @throws IllegalStateException  if the service is already connected or data folder cannot be created
     */
    public void connect() throws SQLException, IllegalStateException {
        if (this.dataSource != null || this.connectionSource != null) {
            throw new IllegalStateException("DatabaseService is already connected.");
        }

        if (!this.dataFolder.exists() && !this.dataFolder.mkdirs()) {
            throw new IllegalStateException("Unable to create data folder: " + this.dataFolder.getAbsolutePath());
        }

        this.dataSource = this.createHikariDataSource();

        DatabaseMode mode = this.databaseConfiguration.databaseMode;
        switch (mode) {
            case SQLITE -> {
                this.dataSource.setDriverClassName("org.sqlite.JDBC");
                this.dataSource.setJdbcUrl("jdbc:sqlite:" + this.dataFolder + "/database.db");
            }

            case MYSQL -> {
                this.dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                this.dataSource.setJdbcUrl("jdbc:mysql://" + this.databaseConfiguration.hostname + ":" + this.databaseConfiguration.port + "/" + this.databaseConfiguration.database);
            }

            default -> throw new IllegalStateException("Unknown database mode: " + mode.name());
        }

        try {
            this.connectionSource = new DataSourceConnectionSource(this.dataSource, this.dataSource.getJdbcUrl());
            this.logger.info("Connected to " + mode.name() + " database.");
        }
        catch (SQLException sqlException) {
            this.logger.log(Level.SEVERE, "Failed to connect to database", sqlException);
            this.dataSource.close(); // We're closing after exception, otherwise can cause a leak
            this.dataSource = null;
            throw sqlException;
        }
    }


    /**
     * Creates a configured instance of {@link HikariDataSource} for connection pooling.
     *
     * @return a new {@link HikariDataSource} configured with pool size and prepared statement caching
     */
    private @NotNull HikariDataSource createHikariDataSource() {
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

    /**
     * Closes the {@link ConnectionSource} and the underlying {@link HikariDataSource}, releasing all resources.
     * <p>
     * Logs warnings if the service was not connected or errors if closing fails.
     */
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

    /**
     * Returns the active {@link ConnectionSource} used for database operations.
     *
     * @return the current {@link ConnectionSource}, or {@code null} if not connected
     */
    public @Nullable ConnectionSource getConnectionSource() {
        return this.connectionSource;
    }
}
