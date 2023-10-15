package com.github.imdmk.spenttime.database;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {

    private final Logger logger;
    private final File dataFolder;
    private final DatabaseSettings databaseSettings;

    private HikariDataSource dataSource;
    private ConnectionSource connectionSource;

    public DatabaseManager(Logger logger, File dataFolder, DatabaseSettings databaseSettings) {
        this.logger = logger;
        this.dataFolder = dataFolder;
        this.databaseSettings = databaseSettings;
    }

    public void connect() throws SQLException {
        this.dataSource = new HikariDataSource();

        this.dataSource.setMaximumPoolSize(5);
        this.dataSource.setUsername(this.databaseSettings.username);
        this.dataSource.setPassword(this.databaseSettings.password);

        this.dataSource.addDataSourceProperty("cachePrepStmts", true);
        this.dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        this.dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        this.dataSource.addDataSourceProperty("useServerPrepStmts", true);

        DatabaseMode databaseMode = this.databaseSettings.databaseMode;
        switch (databaseMode) {
            case SQLITE -> {
                this.dataSource.setDriverClassName("org.sqlite.JDBC");
                this.dataSource.setJdbcUrl("jdbc:sqlite:" + this.dataFolder + "/database.db");
            }

            case MYSQL -> {
                this.dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                this.dataSource.setJdbcUrl("jdbc:mysql://" + this.databaseSettings.hostname + ":" + this.databaseSettings.port + "/" + this.databaseSettings.database);
            }

            case MARIADB -> {
                this.dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
                this.dataSource.setJdbcUrl("jdbc:mariadb://" + this.databaseSettings.hostname + ":" + this.databaseSettings.port + "/" + this.databaseSettings.database);
            }

            default -> throw new IllegalStateException("Unknown database mode: " + databaseMode.name());
        }

        this.connectionSource = new DataSourceConnectionSource(this.dataSource, this.dataSource.getJdbcUrl());

        this.logger.info("Connected to " + databaseMode.name() + " database.");
    }

    public void close() {
        try {
            this.dataSource.close();
            this.connectionSource.close();
        }
        catch (Exception exception) {
            this.logger.log(Level.SEVERE, "An error occurred while closing the database connection", exception);
        }
    }

    public ConnectionSource getConnectionSource() {
        return this.connectionSource;
    }
}
