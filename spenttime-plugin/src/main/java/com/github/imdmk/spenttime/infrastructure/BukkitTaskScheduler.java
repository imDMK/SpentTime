package com.github.imdmk.spenttime.infrastructure;

import com.github.imdmk.spenttime.task.TaskScheduler;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitTaskScheduler implements TaskScheduler {

    private final Plugin plugin;
    private final BukkitScheduler bukkitScheduler;

    public BukkitTaskScheduler(@NotNull Plugin plugin, @NotNull Server server) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
        this.bukkitScheduler = Objects.requireNonNull(server.getScheduler(), "server cannot be null");
    }

    @Override
    public BukkitTask runSync(@NotNull Runnable runnable) {
        return this.bukkitScheduler.runTask(this.plugin, runnable);
    }

    @Override
    public BukkitTask runAsync(@NotNull Runnable runnable) {
        return this.bukkitScheduler.runTaskAsynchronously(this.plugin, runnable);
    }

    @Override
    public BukkitTask runLaterAsync(@NotNull Runnable runnable, long delay) {
        return this.bukkitScheduler.runTaskLaterAsynchronously(this.plugin, runnable, delay);
    }

    @Override
    public BukkitTask runTimerSync(@NotNull Runnable runnable, long delay, long period) {
        return this.bukkitScheduler.runTaskTimer(this.plugin, runnable, delay, period);
    }

    @Override
    public BukkitTask runTimerAsync(@NotNull Runnable runnable, long delay, long period) {
        return this.bukkitScheduler.runTaskTimerAsynchronously(this.plugin, runnable, delay, period);
    }

    @Override
    public void cancelTask(int taskId) {
        this.bukkitScheduler.cancelTask(taskId);
    }

    @Override
    public void shutdown() {
        this.bukkitScheduler.cancelTasks(this.plugin);
    }
}

