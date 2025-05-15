package com.github.imdmk.spenttime.task;

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
    void runSync(@NotNull Runnable runnable);

    /**
     * Executes the given {@link Runnable} asynchronously on a separate thread as soon as possible.
     *
     * @param runnable the task to run asynchronously; must not be null
     * @throws NullPointerException if runnable is null
     */
    void runAsync(@NotNull Runnable runnable);

    /**
     * Executes the given {@link Runnable} asynchronously after the specified delay.
     * The delay unit depends on the implementation (typically server ticks or milliseconds).
     *
     * @param runnable the task to run asynchronously; must not be null
     * @param delay    the delay before executing the task, in implementation-specific units (e.g. ticks)
     * @throws NullPointerException if runnable is null
     */
    void runLaterAsync(@NotNull Runnable runnable, long delay);

    /**
     * Executes the given {@link Runnable} repeatedly on a timer asynchronously,
     * starting after the initial delay and repeating with the specified period.
     * The delay and period units depend on the implementation (e.g. server ticks or milliseconds).
     *
     * @param runnable the task to run asynchronously on a timer; must not be null
     * @param delay    the initial delay before first execution, in implementation-specific units
     * @param period   the period between subsequent executions, in implementation-specific units
     * @throws NullPointerException if runnable is null
     */
    void runTimerAsync(@NotNull Runnable runnable, long delay, long period);
}
