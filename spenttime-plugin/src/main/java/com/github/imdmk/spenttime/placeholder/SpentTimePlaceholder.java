package com.github.imdmk.spenttime.placeholder;

import com.github.imdmk.spenttime.user.BukkitSpentTimeService;
import com.github.imdmk.spenttime.util.DurationUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class SpentTimePlaceholder extends PlaceholderExpansion {

    private final PluginDescriptionFile pluginDescriptionFile;
    private final BukkitSpentTimeService bukkitSpentTimeService;

    public SpentTimePlaceholder(PluginDescriptionFile pluginDescriptionFile, BukkitSpentTimeService bukkitSpentTimeService) {
        this.pluginDescriptionFile = pluginDescriptionFile;
        this.bukkitSpentTimeService = bukkitSpentTimeService;
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

        Duration playerSpentTime = this.bukkitSpentTimeService.getSpentTime(player);
        return DurationUtil.toHumanReadable(playerSpentTime);
    }
}
