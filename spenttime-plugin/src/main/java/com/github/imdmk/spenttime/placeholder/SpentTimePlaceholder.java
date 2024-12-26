package com.github.imdmk.spenttime.placeholder;

import com.github.imdmk.spenttime.user.BukkitPlayerSpentTimeService;
import com.github.imdmk.spenttime.util.DurationUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class SpentTimePlaceholder extends PlaceholderExpansion {

    private final PluginDescriptionFile pluginDescriptionFile;
    private final BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService;

    public SpentTimePlaceholder(PluginDescriptionFile pluginDescriptionFile, BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService) {
        this.pluginDescriptionFile = pluginDescriptionFile;
        this.bukkitPlayerSpentTimeService = bukkitPlayerSpentTimeService;
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
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return null;
        }

        Duration playerSpentTime = this.bukkitPlayerSpentTimeService.getSpentTime(player);
        return DurationUtil.toHumanReadable(playerSpentTime);
    }
}
