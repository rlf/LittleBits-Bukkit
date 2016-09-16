package com.github.rlf.littlebits.async.bukkit;

import com.github.rlf.littlebits.async.Scheduler;
import org.bukkit.scheduler.BukkitTask;

/**
 * A Bukkit implementation of the Task abstraction.
 */
public class TaskBukkit implements Runnable, Scheduler.Task {
    private final Runnable proxy;
    private final boolean repeating;
    private volatile boolean running = false;
    private volatile boolean done = false;
    private BukkitTask task;

    public TaskBukkit(Runnable proxy, boolean repeating) {
        this.proxy = proxy;
        this.repeating = repeating;
    }

    public Scheduler.Task setTask(BukkitTask task) {
        this.task = task;
        return this;
    }

    @Override
    public void run() {
        running = true;
        try {
            proxy.run();
        } finally {
            running = false;
            if (!repeating) {
                done = true;
            }
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public boolean isRepeating() {
        return repeating;
    }

    @Override
    public boolean cancel() {
        if (task != null && !isDone()) {
            task.cancel();
            return true;
        }
        return false;
    }
}
