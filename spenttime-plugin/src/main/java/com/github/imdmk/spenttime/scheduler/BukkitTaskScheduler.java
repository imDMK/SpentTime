package com.github.imdmk.spenttime.scheduler;

import com.github.imdmk.spenttime.task.TaskScheduler;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class BukkitTaskScheduler implements TaskScheduler {

    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public BukkitTaskScheduler(Plugin plugin, Server server) {
        this.plugin = plugin;
        this.scheduler = server.getScheduler();
    }

    @Override
    public void runSync(Runnable runnable) {
        this.scheduler.runTask(this.plugin, runnable);
    }

    @Override
    public void runAsync(Runnable runnable) {
        this.scheduler.runTaskAsynchronously(this.plugin, runnable);
    }

    @Override
    public void runLaterAsync(Runnable runnable, long delay) {
        this.scheduler.runTaskLaterAsynchronously(this.plugin, runnable, delay);
    }

    @Override
    public void runTimerAsync(Runnable runnable, long delay, long period) {
        this.scheduler.runTaskTimerAsynchronously(this.plugin, runnable, delay, period);
    }
}
