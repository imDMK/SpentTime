package com.github.imdmk.spenttime.user.task;

import com.github.imdmk.spenttime.user.BukkitSpentTimeService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class UserSaveSpentTimeTask implements Runnable {

    private final Server server;
    private final UserCache userCache;
    private final UserRepository userRepository;
    private final BukkitSpentTimeService bukkitSpentTimeService;

    public UserSaveSpentTimeTask(Server server, UserRepository userRepository, UserCache userCache, BukkitSpentTimeService bukkitSpentTimeService) {
        this.server = server;
        this.userRepository = userRepository;
        this.userCache = userCache;
        this.bukkitSpentTimeService = bukkitSpentTimeService;
    }

    @Override
    public void run() {
        this.server.getOnlinePlayers().forEach(this::saveSpentTime);
    }

    private void saveSpentTime(Player player) {
        User user = this.userCache.get(player.getUniqueId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.setSpentTime(this.bukkitSpentTimeService.getSpentTime(player));
        this.userRepository.save(user);
    }
}
