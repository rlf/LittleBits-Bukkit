package com.github.rlf.bitcloud.async;

/**
 * Interface for sync and async calls.
 */
public interface Scheduler {
    void sync(Runnable runnable);
    void sync(Runnable runnable, int delayMs);
    void sync(Runnable runnable, int delayMs, int periodMs);
    void async(Runnable runnable);
    void async(Runnable runnable, int delayMs);
    void async(Runnable runnable, int delayMs, int periodMs);
}
