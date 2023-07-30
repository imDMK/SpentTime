package com.github.imdmk.spenttime.configuration;

import com.github.imdmk.spenttime.database.DatabaseMode;
import eu.okaeri.configs.OkaeriConfig;

public class DatabaseConfiguration extends OkaeriConfig {

    public DatabaseMode databaseMode = DatabaseMode.SQLITE;

    public String hostname = "localhost";
    public String database = "database";
    public String username = "root";
    public String password = "ExamplePassword1101";
    public int port = 3306;
}
