package com.github.imdmk.spenttime.user.task;

import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserManager;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.PlayerUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class UserSpentTimeSaveTask implements Runnable {

    private final Server server;
    private final UserRepository userRepository;
    private final UserManager userManager;

    public UserSpentTimeSaveTask(Server server, UserRepository userRepository, UserManager userManager) {
        this.server = server;
        this.userRepository = userRepository;
        this.userManager = userManager;
    }

    @Override
    public void run() {
        for (Player player : this.server.getOnlinePlayers()) {
            this.userManager.getUser(player.getUniqueId()).ifPresent(user -> this.saveSpentTime(player, user));
        }
    }

    private void saveSpentTime(Player player, User user) {
        long spentTime = PlayerUtil.getSpentTime(player);

        user.setSpentTime(spentTime);
        this.userRepository.save(user);
    }
}
