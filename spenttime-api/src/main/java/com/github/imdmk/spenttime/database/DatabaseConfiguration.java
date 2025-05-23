package com.github.imdmk.spenttime.database;

import com.github.imdmk.spenttime.configuration.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

public class DatabaseConfiguration extends ConfigSection {

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

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {};
    }

    @Override
    public @NotNull String getFileName() {
        return "databaseConfiguration.yml";
    }
}
