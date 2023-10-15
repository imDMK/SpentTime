package com.github.imdmk.spenttime.user.listener;

import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserManager;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;
import java.util.UUID;

public class UserCreateListener implements Listener {

    private final UserRepository userRepository;
    private final UserManager userManager;
    private final TaskScheduler taskScheduler;

    public UserCreateListener(UserRepository userRepository, UserManager userManager, TaskScheduler taskScheduler) {
        this.userRepository = userRepository;
        this.userManager = userManager;
        this.taskScheduler = taskScheduler;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        UUID playerUniqueId = player.getUniqueId();
        String playerName = player.getName();

        this.taskScheduler.runAsync(() -> {
            User user = this.userManager.findOrCreateUser(playerUniqueId, playerName);

            boolean updatedSpentTime = this.updateSpentTime(player, user);
            boolean updatedUserName = this.updateUserName(player, user);

            if (updatedSpentTime || updatedUserName) {
                this.userRepository.save(user);
            }
        });
    }

    private boolean updateSpentTime(Player player, User user) {
        Duration playerSpentTime = PlayerUtil.getSpentTime(player);
        Duration userSpentTime = user.getSpentTimeDuration();

        if (playerSpentTime.equals(userSpentTime)) {
            return false;
        }

        user.setSpentTime(playerSpentTime);
        return true;
    }

    private boolean updateUserName(Player player, User user) {
        String playerName = player.getName();
        String userName = user.getName();

        if (playerName.equals(userName)) {
            return false;
        }

        user.setName(playerName);
        return true;
    }
}
