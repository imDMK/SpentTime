package com.github.imdmk.spenttime.task;

import org.jetbrains.annotations.NotNull;

public interface TaskScheduler {

    void runSync(@NotNull Runnable runnable);

    void runAsync(@NotNull Runnable runnable);

    void runLaterAsync(@NotNull Runnable runnable, long delay);

    void runTimerAsync(@NotNull Runnable runnable, long delay, long period);
}
