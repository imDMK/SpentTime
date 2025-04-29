package com.github.imdmk.spenttime.user.controller;

import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserLoadController implements Listener {

    private final Server server;
    private final UserRepository userRepository;
    private final Logger logger;

    public UserLoadController(Server server, UserRepository userRepository, Logger logger) {
        this.server = server;
        this.userRepository = userRepository;
        this.logger = logger;
    }

    @EventHandler
    public void onServerReload(ServerLoadEvent event) {
        if (event.getType() != ServerLoadEvent.LoadType.RELOAD) {
            return;
        }

        this.server.getOnlinePlayers().forEach(this::loadOrCreateUser);
    }

    private void loadOrCreateUser(Player player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();

        this.userRepository.findByUUID(uuid)
                .thenAccept(optionalUser -> {
                    if (optionalUser.isPresent()) {
                        this.createUser(uuid, name);
                    }
                })
                .exceptionally(throwable -> {
                    this.logger.log(Level.SEVERE, "Failed to load user during server reload: " + name, throwable);
                    return null;
                });
    }

    private void createUser(UUID uuid, String name) {
        User user = new User(uuid, name);
        this.userRepository.save(user);
    }
}
