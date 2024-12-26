package com.github.imdmk.spenttime.user.controller;

import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

import java.util.UUID;

public class UserLoadController implements Listener {

    private final Server server;
    private final UserRepository userRepository;

    public UserLoadController(Server server, UserRepository userRepository) {
        this.server = server;
        this.userRepository = userRepository;
    }

    @EventHandler
    public void onServerReload(ServerLoadEvent event) {
        if (event.getType() != ServerLoadEvent.LoadType.RELOAD) {
            return;
        }

        for (Player player : this.server.getOnlinePlayers()) {
            UUID playerUuid = player.getUniqueId();
            String playerName = player.getName();

            this.userRepository.findByUUID(playerUuid)
                    .thenAcceptAsync(userOptional -> {
                        if (userOptional.isEmpty()) {
                            User user = new User(playerUuid, playerName);
                            this.userRepository.save(user);
                        }
                    })
                    .exceptionally(throwable -> {
                        throw new RuntimeException(throwable);
                    });
        }
    }
}
