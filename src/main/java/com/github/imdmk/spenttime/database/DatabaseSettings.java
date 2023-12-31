package com.github.imdmk.spenttime.database;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class DatabaseSettings extends OkaeriConfig {

    @Comment({
            "# Database mode",
            "# Supported databases: SQLITE, MYSQL"
    })
    public DatabaseMode databaseMode = DatabaseMode.SQLITE;

    public String hostname = "localhost";
    public String database = "database";
    public String username = "root";
    public String password = "ExamplePassword1101";
    public int port = 3306;
}
