package com.github.rlf.littlebits.async.generic;

import com.github.rlf.littlebits.async.Scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A generic implementation of the Scheduler (not Bukkit specific).
 */
public class SchedulerExecutorService implements Scheduler {
    private final ScheduledExecutorService asyncScheduler;
    private final ScheduledExecutorService syncScheduler;
    private long syncId;

    public SchedulerExecutorService(int threads) {
        syncScheduler = Executors.newSingleThreadScheduledExecutor();
        asyncScheduler = threads > 0 ? Executors.newScheduledThreadPool(threads) : syncScheduler;
        syncScheduler.submit(new Runnable() {
            @Override
            public void run() {
                syncId = Thread.currentThread().getId();
            }
        });
    }

    @Override
    public boolean isSync() {
        return Thread.currentThread().getId() == syncId;
    }

    @Override
    public Task sync(Runnable runnable) {
        TaskFuture task = new TaskFuture(runnable, false);
        Future<?> future = syncScheduler.submit(task);
        task.setFuture(future);
        return task;
    }

    @Override
    public Task sync(Runnable runnable, int delayMs) {
        TaskFuture task = new TaskFuture(runnable, false);
        Future<?> future = syncScheduler.schedule(task, delayMs, TimeUnit.MILLISECONDS);
        task.setFuture(future);
        return task;
    }

    @Override
    public Task sync(Runnable runnable, int delayMs, int periodMs) {
        TaskFuture task = new TaskFuture(runnable, true);
        Future<?> future = syncScheduler.scheduleAtFixedRate(task, delayMs, periodMs, TimeUnit.MILLISECONDS);
        task.setFuture(future);
        return task;
    }

    @Override
    public Task async(Runnable runnable) {
        TaskFuture task = new TaskFuture(runnable, false);
        Future<?> future = asyncScheduler.submit(task);
        task.setFuture(future);
        return task;
    }

    @Override
    public Task async(Runnable runnable, int delayMs) {
        TaskFuture task = new TaskFuture(runnable, false);
        Future<?> future = asyncScheduler.schedule(task, delayMs, TimeUnit.MILLISECONDS);
        task.setFuture(future);
        return task;
    }

    @Override
    public Task async(Runnable runnable, int delayMs, int periodMs) {
        TaskFuture task = new TaskFuture(runnable, true);
        Future<?> future = asyncScheduler.scheduleAtFixedRate(task, delayMs, periodMs, TimeUnit.MILLISECONDS);
        task.setFuture(future);
        return task;
    }

    @Override
    public void shutdown() {
        asyncScheduler.shutdown();
        syncScheduler.shutdown();
    }
}
