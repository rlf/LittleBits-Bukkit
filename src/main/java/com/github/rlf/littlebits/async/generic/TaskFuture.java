package com.github.rlf.littlebits.async.generic;

import com.github.rlf.littlebits.async.Scheduler;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

/**
 * A Task wrapping a Future
 */
public class TaskFuture implements Scheduler.Task {
    private final Runnable runnable;
    private final boolean repeating;
    private Future<?> future;
    private boolean running;

    public TaskFuture(Runnable runnable, boolean repeating) {
        this.runnable = runnable;
        this.repeating = repeating;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isDone() {
        return future != null && future.isDone();
    }

    @Override
    public boolean isRepeating() {
        return future instanceof ScheduledFuture && repeating;
    }

    @Override
    public boolean cancel() {
        if (future != null) {
            return future.cancel(true);
        }
        return false;
    }

    @Override
    public void run() {
        running = true;
        try {
            runnable.run();
        } finally {
            running = false;
        }
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }
}
