package com.github.imdmk.spenttime.user.controller;

import com.github.imdmk.spenttime.user.BukkitPlayerSpentTimeService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserSaveController implements Listener {

    private final UserCache userCache;
    private final UserRepository userRepository;
    private final BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService;

    public UserSaveController(UserCache userCache, UserRepository userRepository, BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService) {
        this.userCache = userCache;
        this.userRepository = userRepository;
        this.bukkitPlayerSpentTimeService = bukkitPlayerSpentTimeService;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        User user = this.userCache.get(player.getUniqueId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.setSpentTime(this.bukkitPlayerSpentTimeService.getSpentTime(player));

        this.userRepository.save(user);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        User user = this.userCache.get(player.getUniqueId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.setSpentTime(this.bukkitPlayerSpentTimeService.getSpentTime(player));

        this.userRepository.save(user);
    }
}
