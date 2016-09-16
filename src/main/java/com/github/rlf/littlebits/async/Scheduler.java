package com.github.rlf.littlebits.async;

/**
 * Interface for sync and async calls.
 */
public interface Scheduler {
    /**
     * Whether or not the current thread is the synchronous Thread.
     * @return Whether or not the current thread is the synchronous Thread.
     */
    boolean isSync();

    /**
     * Execute a runnable synchronously (on the main-server-thread).
     * @param runnable The runnable to execute
     */
    Task sync(Runnable runnable);

    /**
     * Execute a runnable synchronously (on the main-server-thread), after a fixed delay in milliseconds.
     * @param runnable The runnable to execute
     */
    Task sync(Runnable runnable, int delayMs);

    /**
     * Execute a runnable synchronously (on the main-server-thread), after a fixed delay in milliseconds,
     * and repeat it periodically.
     * @param runnable The runnable to execute
     */
    Task sync(Runnable runnable, int delayMs, int periodMs);

    /**
     * Execute a runnable asynchronously (on the main-server-thread).
     * @param runnable The runnable to execute
     */
    Task async(Runnable runnable);

    /**
     * Execute a runnable asynchronously (on the main-server-thread), after a fixed delay in milliseconds.
     * @param runnable The runnable to execute
     */
    Task async(Runnable runnable, int delayMs);

    /**
     * Execute a runnable asynchronously (on the main-server-thread), after a fixed delay in milliseconds,
     * and repeat it periodically.
     * @param runnable The runnable to execute
     */
    Task async(Runnable runnable, int delayMs, int periodMs);

    void shutdown();

    interface Task extends Runnable {
        /**
         * Whether or not the task is currently executing.
         * @return Whether or not the task is currently executing.
         */
        boolean isRunning();

        /**
         * Whether or not the task is done (note repeating tasks are never done).
         * @return Whether or not the task is done.
         */
        boolean isDone();

        /**
         * Whether or not the task is a repeating task (a timer).
         * @return Whether or not the task is a repeating task
         */
        boolean isRepeating();

        /**
         * Cancels the task.
         * @return <code>true</code> if successful.
         */
        boolean cancel();
    }
}
