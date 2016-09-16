package com.github.rlf.littlebits.async.bukkit;

import com.github.rlf.littlebits.async.Scheduler;
import dk.lockfuglsang.minecraft.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Bukkit implementation of Scheduler
 */
public class SchedulerBukkit implements Scheduler {
    private final JavaPlugin plugin;
    private final BukkitScheduler bukkitScheduler;

    public SchedulerBukkit(JavaPlugin plugin, BukkitScheduler bukkitScheduler) {
        this.plugin = plugin;
        this.bukkitScheduler = bukkitScheduler;
    }

    @Override
    public boolean isSync() {
        return Bukkit.isPrimaryThread();
    }

    @Override
    public Task sync(Runnable runnable) {
        TaskBukkit task = new TaskBukkit(runnable, false);
        return task.setTask(bukkitScheduler.runTask(plugin, task));
    }

    @Override
    public Task sync(Runnable runnable, int delayMs) {
        TaskBukkit task = new TaskBukkit(runnable, false);
        return task.setTask(bukkitScheduler.runTaskLater(plugin, task, TimeUtil.millisAsTicks(delayMs)));
    }

    @Override
    public Task sync(Runnable runnable, int delayMs, int periodMs) {
        TaskBukkit task = new TaskBukkit(runnable, true);
        return task.setTask(bukkitScheduler.runTaskTimer(plugin, task, TimeUtil.millisAsTicks(delayMs), TimeUtil.millisAsTicks(periodMs)));
    }

    @Override
    public Task async(Runnable runnable) {
        TaskBukkit task = new TaskBukkit(runnable, false);
        return task.setTask(bukkitScheduler.runTaskAsynchronously(plugin, task));
    }

    @Override
    public Task async(Runnable runnable, int delayMs) {
        TaskBukkit task = new TaskBukkit(runnable, false);
        return task.setTask(bukkitScheduler.runTaskLaterAsynchronously(plugin, task, TimeUtil.millisAsTicks(delayMs)));
    }

    @Override
    public Task async(Runnable runnable, int delayMs, int periodMs) {
        TaskBukkit task = new TaskBukkit(runnable, true);
        return task.setTask(bukkitScheduler.runTaskTimerAsynchronously(plugin, task, TimeUtil.millisAsTicks(delayMs), TimeUtil.millisAsTicks(periodMs)));
    }

    @Override
    public void shutdown() {
        // TODO: 14/09/2016 - R4zorax: Keep track of tasks?
    }
}
