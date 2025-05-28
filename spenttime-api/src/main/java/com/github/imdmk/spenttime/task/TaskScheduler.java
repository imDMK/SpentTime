package com.github.imdmk.spenttime.task;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Provides methods for scheduling tasks to be executed either synchronously on the main thread
 * or asynchronously on a separate thread.
 * <p>
 * Implementations of this interface should ensure that synchronous tasks run on the main server thread,
 * while asynchronous tasks run off the main thread to prevent blocking.
 */
public interface TaskScheduler {

    /**
     * Executes the given {@link Runnable} synchronously on the main server thread as soon as possible.
     *
     * @param runnable the task to run synchronously; must not be null
     * @throws NullPointerException if runnable is null
     */
    BukkitTask runSync(@NotNull Runnable runnable);

    /**
     * Executes the given {@link Runnable} asynchronously on a separate thread as soon as possible.
     *
     * @param runnable the task to run asynchronously; must not be null
     * @throws NullPointerException if runnable is null
     */
    BukkitTask runAsync(@NotNull Runnable runnable);

    /**
     * Executes the given {@link Runnable} asynchronously after the specified delay.
     * The delay unit depends on the implementation (typically server ticks or milliseconds).
     *
     * @param runnable the task to run asynchronously; must not be null
     * @param delay    the delay before executing the task, in implementation-specific units (e.g. ticks)
     * @throws NullPointerException if runnable is null
     */
    BukkitTask runLaterAsync(@NotNull Runnable runnable, long delay);

    /**
     * Executes the given {@link Runnable} repeatedly on a timer synchronously,
     * starting after the initial delay and repeating with the specified period.
     * The delay and period units depend on the implementation (e.g., server ticks or milliseconds).
     *
     * @param runnable the task to run synchronously on a timer; must not be null
     * @param delay    the initial delay before first execution, in implementation-specific units
     * @param period   the period between later executions, in implementation-specific units
     * @throws NullPointerException if runnable is null
     */
    BukkitTask runTimerSync(@NotNull Runnable runnable, long delay, long period);

    /**
     * Executes the given {@link Runnable} repeatedly on a timer asynchronously,
     * starting after the initial delay and repeating with the specified period.
     * The delay and period units depend on the implementation (e.g., server ticks or milliseconds).
     *
     * @param runnable the task to run asynchronously on a timer; must not be null
     * @param delay    the initial delay before first execution, in implementation-specific units
     * @param period   the period between later executions, in implementation-specific units
     * @throws NullPointerException if runnable is null
     */
    BukkitTask runTimerAsync(@NotNull Runnable runnable, long delay, long period);

    /**
     * Cancels a scheduled task identified by its task ID.
     * <p>
     * If the task with the specified ID is currently scheduled or running, it will be cancelled,
     * preventing any future executions of the task. If no task with the given ID exists or
     * it has already completed or been cancelled, this method has no effect.
     * </p>
     *
     * @param taskId the unique identifier of the scheduled task to cancel
     */
    void cancelTask(int taskId);

    /**
     * Shuts down the scheduler and cancels all pending asynchronous tasks.
     * <p>
     * This method should be called during plugin disable or application shutdown
     * to ensure that no background tasks continue running after the application stops.
     * After calling this method, the scheduler should reject any new tasks.
     * </p>
     *
     * @implNote Implementations should ensure proper termination of all internal executor services
     *           or scheduling mechanisms to prevent resource leaks or thread hangs.
     */
    void shutdown();
}
