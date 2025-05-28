package com.github.imdmk.spenttime.task;

/**
 * Represents a cancellable scheduled plugin task.
 */
public interface PluginTask extends Runnable {

    /**
     * Returns whether the task has been stopped and should no longer execute.
     *
     * @return true if the task is stopped; false otherwise
     */
    boolean isStopped();

    /**
     * Safely stops the task, preventing further execution.
     */
    void stop();
}
