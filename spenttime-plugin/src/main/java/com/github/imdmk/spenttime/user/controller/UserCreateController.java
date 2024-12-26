package com.github.imdmk.spenttime.user.controller;

import com.github.imdmk.spenttime.user.BukkitPlayerSpentTimeService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserService;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;
import java.util.UUID;

public class UserCreateController implements Listener {

    private final UserRepository userRepository;
    private final UserService userService;
    private final BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService;

    public UserCreateController(UserRepository userRepository, UserService userService, BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.bukkitPlayerSpentTimeService = bukkitPlayerSpentTimeService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        UUID playerUniqueId = player.getUniqueId();
        String playerName = player.getName();

        this.userService.getOrFindUser(playerUniqueId)
                .thenAccept(userOptional -> {
                    if (userOptional.isEmpty()) {
                        User user = new User(playerUniqueId, playerName);
                        this.userRepository.save(user);
                        return;
                    }

                    User user = userOptional.get();

                    boolean needUpdateSpentTime = this.checkIfNeedUpdateSpentTime(player, user);
                    boolean needUpdateUserName = this.checkIfNeedUpdateUserName(player, user);

                    if (needUpdateSpentTime || needUpdateUserName) {
                        this.userRepository.save(user);
                    }
                })
                .exceptionally(throwable -> {
                    throw new RuntimeException(throwable);
                });
    }

    private boolean checkIfNeedUpdateSpentTime(Player player, User user) {
        Duration playerSpentTime = this.bukkitPlayerSpentTimeService.getSpentTime(player);
        Duration userSpentTime = user.getSpentTimeDuration();

        if (playerSpentTime.equals(userSpentTime)) {
            return false;
        }

        user.setSpentTime(playerSpentTime);
        return true;
    }

    private boolean checkIfNeedUpdateUserName(Player player, User user) {
        String playerName = player.getName();
        String userName = user.getName();

        if (playerName.equals(userName)) {
            return false;
        }

        user.setName(playerName);
        return true;
    }
}