package com.github.imdmk.spenttime.placeholder.implementation;

import com.github.imdmk.spenttime.util.DurationUtil;
import com.github.imdmk.spenttime.util.PlayerUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class SpentTimeFormattedPlaceholder extends PlaceholderExpansion {

    private final PluginDescriptionFile pluginDescriptionFile;

    public SpentTimeFormattedPlaceholder(PluginDescriptionFile pluginDescriptionFile) {
        this.pluginDescriptionFile = pluginDescriptionFile;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "spent-time-formatted";
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
            return null;
        }

        Duration playerSpentTime = PlayerUtil.getSpentTimeDuration(player);
        return DurationUtil.toHumanReadable(playerSpentTime);
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return null;
        }

        Duration playerSpentTime = PlayerUtil.getSpentTimeDuration(player);
        return DurationUtil.toHumanReadable(playerSpentTime);
    }
}
