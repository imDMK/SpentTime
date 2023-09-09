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
    private final DatabaseConfiguration databaseConfiguration;

    private HikariDataSource dataSource;
    private ConnectionSource connectionSource;

    public DatabaseManager(Logger logger, File dataFolder, DatabaseConfiguration databaseConfiguration) {
        this.logger = logger;
        this.dataFolder = dataFolder;
        this.databaseConfiguration = databaseConfiguration;
    }

    public void connect() throws SQLException {
        this.dataSource = new HikariDataSource();

        this.dataSource.setMaximumPoolSize(5);
        this.dataSource.setUsername(this.databaseConfiguration.username);
        this.dataSource.setPassword(this.databaseConfiguration.password);

        this.dataSource.addDataSourceProperty("cachePrepStmts", true);
        this.dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        this.dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        this.dataSource.addDataSourceProperty("useServerPrepStmts", true);

        String databaseModeName = this.databaseConfiguration.databaseMode.name().toUpperCase();
        DatabaseMode databaseMode = DatabaseMode.valueOf(databaseModeName);

        switch (databaseMode) {
            case SQLITE -> {
                this.dataSource.setDriverClassName("org.sqlite.JDBC");
                this.dataSource.setJdbcUrl("jdbc:sqlite:" + this.dataFolder + "/database.db");
            }

            case MYSQL -> {
                this.dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                this.dataSource.setJdbcUrl("jdbc:mysql://" + this.databaseConfiguration.hostname + ":" + this.databaseConfiguration.port + "/" + this.databaseConfiguration.database);
            }

            case MARIADB -> {
                this.dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
                this.dataSource.setJdbcUrl("jdbc:mariadb://" + this.databaseConfiguration.hostname + ":" + this.databaseConfiguration.port + "/" + this.databaseConfiguration.database);
            }

            default -> throw new IllegalStateException("Unknown database mode: " + databaseModeName);
        }

        this.connectionSource = new DataSourceConnectionSource(this.dataSource, this.dataSource.getJdbcUrl());

        this.logger.info("Connected to " + databaseModeName + " database.");
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
