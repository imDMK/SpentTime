package com.github.imdmk.spenttime.user.task;

import com.github.imdmk.spenttime.user.BukkitPlayerSpentTimeService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class UserSaveSpentTimeTask implements Runnable {

    private final Server server;
    private final UserCache userCache;
    private final UserRepository userRepository;
    private final BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService;

    public UserSaveSpentTimeTask(Server server, UserRepository userRepository, UserCache userCache, BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService) {
        this.server = server;
        this.userRepository = userRepository;
        this.userCache = userCache;
        this.bukkitPlayerSpentTimeService = bukkitPlayerSpentTimeService;
    }

    @Override
    public void run() {
        for (Player player : this.server.getOnlinePlayers()) {
            this.userCache.get(player.getUniqueId()).ifPresent(user -> this.saveSpentTime(player, user));
        }
    }

    private void saveSpentTime(Player player, User user) {
        user.setSpentTime(this.bukkitPlayerSpentTimeService.getSpentTime(player));
        this.userRepository.save(user);
    }
}
