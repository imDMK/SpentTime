package com.github.imdmk.spenttime.database;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class DatabaseConfiguration extends OkaeriConfig {

    @Comment("# Database modes: SQLITE, MYSQL, MARIADB")
    public DatabaseMode databaseMode = DatabaseMode.SQLITE;

    public String hostname = "localhost";
    public String database = "database";
    public String username = "root";
    public String password = "ExamplePassword1101";
    public int port = 3306;
}
