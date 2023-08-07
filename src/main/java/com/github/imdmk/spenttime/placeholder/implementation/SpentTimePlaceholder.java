package com.github.imdmk.spenttime.placeholder.implementation;

import com.github.imdmk.spenttime.util.PlayerUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpentTimePlaceholder extends PlaceholderExpansion {

    private final PluginDescriptionFile pluginDescriptionFile;

    public SpentTimePlaceholder(PluginDescriptionFile pluginDescriptionFile) {
        this.pluginDescriptionFile = pluginDescriptionFile;
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
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "0";
        }

        long playerSpentTime = PlayerUtil.getSpentTime(player);
        return String.valueOf(playerSpentTime);
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "0";
        }

        long playerSpentTime = PlayerUtil.getSpentTime(player);
        return String.valueOf(playerSpentTime);
    }
}
