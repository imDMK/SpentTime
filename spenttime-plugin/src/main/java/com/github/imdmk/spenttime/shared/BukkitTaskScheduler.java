package com.github.imdmk.spenttime.shared;

import com.github.imdmk.spenttime.task.TaskScheduler;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class BukkitTaskScheduler implements TaskScheduler {

    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public BukkitTaskScheduler(@NotNull Plugin plugin, @NotNull Server server) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
        this.scheduler = Objects.requireNonNull(server.getScheduler(), "server scheduler cannot be null");
    }

    @Override
    public void runSync(@NotNull Runnable runnable) {
        this.scheduler.runTask(this.plugin, runnable);
    }

    @Override
    public void runAsync(@NotNull Runnable runnable) {
        this.scheduler.runTaskAsynchronously(this.plugin, runnable);
    }

    @Override
    public void runLaterAsync(@NotNull Runnable runnable, long delay) {
        this.scheduler.runTaskLaterAsynchronously(this.plugin, runnable, delay);
    }

    @Override
    public void runTimerAsync(@NotNull Runnable runnable, long delay, long period) {
        this.scheduler.runTaskTimerAsynchronously(this.plugin, runnable, delay, period);
    }
}
