package com.github.imdmk.spenttime.scheduler;

public interface TaskScheduler {

    void runAsync(Runnable runnable);

    void runLater(Runnable runnable);

    void runLaterAsync(Runnable runnable, long delay);

    void runTimerAsync(Runnable runnable, long delay, long period);
}
