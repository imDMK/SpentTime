package com.github.imdmk.spenttime.infrastructure.command.configurator;

import eu.okaeri.configs.OkaeriConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubCommand extends OkaeriConfig {

    public String name;
    public boolean enabled;

    public List<String> aliases = new ArrayList<>();
    public List<String> permissions = new ArrayList<>();

    public SubCommand() {}

    public SubCommand(@NotNull String name, boolean enabled, @NotNull List<String> aliases, @NotNull List<String> permissions) {
        this.name = name;
        this.enabled = enabled;
        this.aliases = aliases;
        this.permissions = permissions;
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
}
