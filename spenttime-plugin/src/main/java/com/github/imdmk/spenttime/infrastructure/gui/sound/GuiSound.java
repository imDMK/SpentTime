package com.github.imdmk.spenttime.infrastructure.gui.sound;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record GuiSound(@NotNull Sound sound, float volume, float pitch) {

    /**
     * Plays the configured sound effect for the specified player at their current location.
     *
     * @param player the {@link Player} to play the sound for (must not be null)
     */
    public void play(@NotNull Player player) {
        player.playSound(player.getLocation(), this.sound, this.volume, this.pitch);
    }
}
