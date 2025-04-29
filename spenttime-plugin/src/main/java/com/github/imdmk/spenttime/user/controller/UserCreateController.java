package com.github.imdmk.spenttime.user.controller;

import com.github.imdmk.spenttime.user.BukkitSpentTimeService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserCreateController implements Listener {

    private final UserRepository userRepository;
    private final BukkitSpentTimeService bukkitSpentTimeService;
    private final Logger logger;

    public UserCreateController(UserRepository userRepository, BukkitSpentTimeService bukkitSpentTimeService, Logger logger) {
        this.userRepository = userRepository;
        this.bukkitSpentTimeService = bukkitSpentTimeService;
        this.logger = logger;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        UUID uuid = player.getUniqueId();
        String name = player.getName();

        this.userRepository.findByUUID(uuid)
                .thenCompose(optionalUser -> {
                    if (optionalUser.isEmpty()) {
                        User newUser = new User(uuid, name);
                        return this.userRepository.save(newUser);
                    }

                    User existingUser = optionalUser.get();
                    if (this.updateUserData(existingUser, player)) {
                        return this.userRepository.save(existingUser);
                    }

                    return CompletableFuture.completedFuture(null);
                })
                .exceptionally(ex -> {
                    this.logger.log(Level.SEVERE, "Failed to process player join for " + name, ex);
                    return null;
                });
    }

    /**
     * Updates the user object based on player data. Returns true if any data changed.
     */
    private boolean updateUserData(User user, Player player) {
        boolean updated = false;

        String playerName = player.getName();
        Duration playerSpentTime = this.bukkitSpentTimeService.getSpentTime(player);

        if (!playerName.equals(user.getName())) {
            user.setName(playerName);
            updated = true;
        }

        if (!playerSpentTime.equals(user.getSpentTimeAsDuration())) {
            user.setSpentTime(playerSpentTime);
            updated = true;
        }

        return updated;
    }
}