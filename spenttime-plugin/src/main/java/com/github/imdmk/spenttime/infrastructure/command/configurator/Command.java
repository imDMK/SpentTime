package com.github.imdmk.spenttime.infrastructure.command.configurator;

import eu.okaeri.configs.OkaeriConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Command extends OkaeriConfig {

    public String name;

    public boolean enabled = true;

    public List<String> aliases = new ArrayList<>();
    public List<String> permissions = new ArrayList<>();

    public Map<String, SubCommand> subCommands = new HashMap<>();

    public Command() {}

    public Command(@NotNull String name) {
        this.name = name;
    }

    public Command(@NotNull String name, @NotNull List<String> aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public Command(@NotNull String name, @NotNull List<String> aliases, @NotNull List<String> permissions) {
        this.name = name;
        this.aliases = aliases;
        this.permissions = permissions;
    }

    public Command(@NotNull String name, boolean enabled, @NotNull List<String> aliases, @NotNull List<String> permissions) {
        this.name = name;
        this.enabled = enabled;
        this.aliases = aliases;
        this.permissions = permissions;
    }

    public Command(
            @NotNull String name,
            boolean enabled,
            @NotNull List<String> aliases,
            @NotNull List<String> permissions,
            @NotNull Map<String, SubCommand> subCommands
    ) {
        this.name = name;
        this.enabled = enabled;
        this.aliases = aliases;
        this.permissions = permissions;
        this.subCommands = subCommands;
    }

    public @NotNull String name() {
        return this.name;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public @NotNull List<String> aliases() {
        return Collections.unmodifiableList(this.aliases);
    }

    public @NotNull List<String> permissions() {
        return Collections.unmodifiableList(this.permissions);
    }

    public @NotNull Map<String, SubCommand> subCommands() {
        return Collections.unmodifiableMap(this.subCommands);
    }
}

