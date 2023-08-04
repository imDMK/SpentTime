package com.github.imdmk.spenttime.user.task;

import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserManager;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.PlayerUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Optional;

public class UserTimeSaveTask implements Runnable {

    private final Server server;
    private final UserRepository userRepository;
    private final UserManager userManager;

    public UserTimeSaveTask(Server server, UserRepository userRepository, UserManager userManager) {
        this.server = server;
        this.userRepository = userRepository;
        this.userManager = userManager;
    }

    @Override
    public void run() {
        for (Player player : this.server.getOnlinePlayers()) {
            Optional<User> userOptional = this.userManager.getUser(player.getUniqueId());
            if (userOptional.isEmpty()) {
                return;
            }

            User user = userOptional.get();

            user.setSpentTime(PlayerUtil.getSpentTime(player));
            this.userRepository.save(user);
        }
    }
}
