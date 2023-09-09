package com.github.imdmk.spenttime.user.listener;

import com.github.imdmk.spenttime.user.UserManager;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class UserLoadListener implements Listener {

    private final Server server;
    private final UserManager userManager;

    public UserLoadListener(Server server, UserManager userManager) {
        this.server = server;
        this.userManager = userManager;
    }

    @EventHandler
    public void onServerReload(ServerLoadEvent event) {
        if (event.getType() != ServerLoadEvent.LoadType.RELOAD) {
            return;
        }

        for (Player player : this.server.getOnlinePlayers()) {
            this.userManager.findOrCreateUser(player.getUniqueId(), player.getName());
        }
    }
}
