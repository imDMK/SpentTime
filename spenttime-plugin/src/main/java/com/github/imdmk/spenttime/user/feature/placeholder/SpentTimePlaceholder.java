package com.github.imdmk.spenttime.user.feature.placeholder;

import com.github.imdmk.spenttime.infrastructure.BukkitSpentTime;
import com.github.imdmk.spenttime.util.DurationUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SpentTimePlaceholder extends PlaceholderExpansion {

    private final PluginDescriptionFile descriptionFile;
    private final BukkitSpentTime bukkitSpentTime;

    public SpentTimePlaceholder(@NotNull PluginDescriptionFile descriptionFile, @NotNull BukkitSpentTime bukkitSpentTime) {
        this.descriptionFile = Objects.requireNonNull(descriptionFile, "descriptionFile cannot be null");
        this.bukkitSpentTime = Objects.requireNonNull(bukkitSpentTime, "bukkitSpentTime cannot be null");
    }

    @Override
    public @NotNull String getIdentifier() {
        return "spent-time";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", this.descriptionFile.getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return this.descriptionFile.getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        return DurationUtil.format(this.bukkitSpentTime.getSpentTime(player));
    }

    @Override
    public @Nullable String onRequest(@NotNull OfflinePlayer player, @NotNull String params) {
        return DurationUtil.format(this.bukkitSpentTime.getSpentTime(player));
    }
}
