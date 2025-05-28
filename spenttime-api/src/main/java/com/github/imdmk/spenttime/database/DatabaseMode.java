package com.github.imdmk.spenttime.database;

/**
 * Represents the supported database modes for data storage.
 * This enum is used to select between local (SQLite) or remote (MySQL) database providers.
 */
public enum DatabaseMode {
    SQLITE,
    MYSQL
}
