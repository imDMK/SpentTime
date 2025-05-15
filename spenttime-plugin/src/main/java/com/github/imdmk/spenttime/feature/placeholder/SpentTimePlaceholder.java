package com.github.imdmk.spenttime.feature.placeholder;

import com.github.imdmk.spenttime.user.BukkitSpentTime;
import com.github.imdmk.spenttime.util.DurationUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpentTimePlaceholder extends PlaceholderExpansion {

    private final PluginDescriptionFile pluginDescriptionFile;
    private final BukkitSpentTime bukkitBukkitSpentTime;

    public SpentTimePlaceholder(@NotNull PluginDescriptionFile pluginDescriptionFile, @NotNull BukkitSpentTime bukkitBukkitSpentTime) {
        this.pluginDescriptionFile = pluginDescriptionFile;
        this.bukkitBukkitSpentTime = bukkitBukkitSpentTime;
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
        return DurationUtil.format(this.bukkitBukkitSpentTime.getSpentTime(player));
    }
}
