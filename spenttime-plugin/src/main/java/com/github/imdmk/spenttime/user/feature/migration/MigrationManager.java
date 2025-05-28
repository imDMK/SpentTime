package com.github.imdmk.spenttime.user.feature.migration;

import com.github.imdmk.spenttime.task.TaskScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Manages the scheduling and cancellation of the {@link MigrationTask}.
 * Ensures that only one migration task is running at a time.
 */
public class MigrationManager {

    private static final long DEFAULT_DELAY_TICKS = 20 * 5; // 5 seconds

    private final TaskScheduler taskScheduler;
    private BukkitTask activeTask;

    /**
     * Constructs a new {@code MigrationManager} with the specified task scheduler.
     *
     * @param taskScheduler the scheduler used to run migration tasks; must not be null
     */
    public MigrationManager(@NotNull TaskScheduler taskScheduler) {
        this.taskScheduler = Objects.requireNonNull(taskScheduler, "taskScheduler cannot be null");
    }

    /**
     * Schedules the given {@link MigrationTask} with the default execution delay.
     *
     * @param task the migration task to schedule; must not be null
     * @return {@code true} if the task was scheduled; {@code false} if a task is already running
     */
    public boolean schedule(@NotNull MigrationTask task) {
        return this.schedule(task, DEFAULT_DELAY_TICKS);
    }

    /**
     * Schedules the given {@link MigrationTask} with a custom delay between executions.
     *
     * @param task       the migration task to schedule; must not be null
     * @param delayTicks the delay (in ticks) between each task execution
     * @return {@code true} if the task was scheduled; {@code false} if a task is already running
     */
    public boolean schedule(@NotNull MigrationTask task, long delayTicks) {
        if (this.activeTask != null) {
            return false;
        }

        this.activeTask = this.taskScheduler.runTimerAsync(task, 1L, delayTicks);
        return true;
    }

    /**
     * Cancels the currently running migration task, if any.
     * This method is safe to call even if no task is scheduled.
     */
    public void cancel() {
        if (this.activeTask == null) {
            return;
        }

        if (this.activeTask.getTaskId() != -1) {
            this.activeTask.cancel();
        }

        this.activeTask = null;
    }

    /**
     * Checks whether a migration task is currently scheduled.
     *
     * @return {@code true} if a task is currently running; {@code false} otherwise
     */
    public boolean isScheduled() {
        return this.activeTask != null;
    }
}
