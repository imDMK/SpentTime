package com.github.imdmk.spenttime.scheduler;

public interface TaskScheduler {

    void runSync(Runnable runnable);

    void runAsync(Runnable runnable);

    void runLaterAsync(Runnable runnable, long delay);

    void runTimerAsync(Runnable runnable, long delay, long period);
}