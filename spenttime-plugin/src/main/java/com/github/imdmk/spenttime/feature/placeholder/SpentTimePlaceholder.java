package com.github.imdmk.spenttime.feature.placeholder;

import com.github.imdmk.spenttime.user.BukkitSpentTime;
import com.github.imdmk.spenttime.util.DurationUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SpentTimePlaceholder extends PlaceholderExpansion {

    private final PluginDescriptionFile pluginDescriptionFile;
    private final BukkitSpentTime bukkitSpentTime;

    public SpentTimePlaceholder(@NotNull PluginDescriptionFile pluginDescriptionFile, @NotNull BukkitSpentTime bukkitSpentTime) {
        this.pluginDescriptionFile = Objects.requireNonNull(pluginDescriptionFile, "pluginDescriptionFile cannot be null");
        this.bukkitSpentTime = Objects.requireNonNull(bukkitSpentTime, "bukkitSpentTime cannot be null");
    }

    @Override
    public @NotNull String getIdentifier() {
        return "spent-time";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", this.pluginDescriptionFile.getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return this.pluginDescriptionFile.getVersion();
    }

    @Override
    public @Nullable String onRequest(@NotNull OfflinePlayer player, @NotNull String params) {
        return DurationUtil.format(this.bukkitSpentTime.getSpentTime(player));
    }
}
