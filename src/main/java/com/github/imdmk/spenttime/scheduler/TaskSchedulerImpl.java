package com.github.imdmk.spenttime.scheduler;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

public final class TaskSchedulerImpl implements TaskScheduler {

    private final Plugin plugin;
    private final Server server;

    public TaskSchedulerImpl(Plugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    public void runAsync(Runnable runnable) {
        this.server.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }

    @Override
    public void runLater(Runnable runnable) {
        this.server.getScheduler().runTask(this.plugin, runnable);
    }

    @Override
    public void runLaterAsync(Runnable runnable, long delay) {
        this.server.getScheduler().runTaskLaterAsynchronously(this.plugin, runnable, delay);
    }

    @Override
    public void runTimerAsync(Runnable runnable, long delay, long period) {
        this.server.getScheduler().runTaskTimerAsynchronously(this.plugin, runnable, delay, period);
    }
}
