package com.github.imdmk.spenttime.user.listener;

import com.github.imdmk.spenttime.task.TaskScheduler;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserManager;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserSaveListener implements Listener {

    private final UserManager userManager;
    private final UserRepository userRepository;
    private final TaskScheduler taskScheduler;

    public UserSaveListener(UserManager userManager, UserRepository userRepository, TaskScheduler taskScheduler) {
        this.userManager = userManager;
        this.userRepository = userRepository;
        this.taskScheduler = taskScheduler;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        User user = this.userManager.getUser(player.getUniqueId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.setSpentTime(PlayerUtil.getSpentTime(player));

        this.taskScheduler.runAsync(() -> this.userRepository.save(user));
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        User user = this.userManager.getUser(player.getUniqueId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.setSpentTime(PlayerUtil.getSpentTime(player));

        this.taskScheduler.runAsync(() -> this.userRepository.save(user));
    }
}
