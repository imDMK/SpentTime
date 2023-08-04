package com.github.imdmk.spenttime.user.listener;

import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class UserCreateListener implements Listener {

    private final UserManager userManager;
    private final TaskScheduler taskScheduler;

    public UserCreateListener(UserManager userManager, TaskScheduler taskScheduler) {
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

            if (!this.isUserHasValidName(user.getName(), playerName)) {
                user.setName(playerName);
            }
        });
    }

    private boolean isUserHasValidName(String userName, String playerName) {
        return userName.equals(playerName);
    }
}
