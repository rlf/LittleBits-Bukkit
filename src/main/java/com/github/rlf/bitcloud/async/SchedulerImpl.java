package com.github.rlf.bitcloud.async;

import dk.lockfuglsang.minecraft.util.TimeUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Bukkit implementation of Scheduler
 */
public class SchedulerImpl implements Scheduler {
    private final JavaPlugin plugin;
    private final BukkitScheduler bukkitScheduler;

    public SchedulerImpl(JavaPlugin plugin, BukkitScheduler bukkitScheduler) {
        this.plugin = plugin;
        this.bukkitScheduler = bukkitScheduler;
    }

    @Override
    public void sync(Runnable runnable) {
        bukkitScheduler.runTask(plugin, runnable);
    }

    @Override
    public void sync(Runnable runnable, int delayMs) {
        bukkitScheduler.runTaskLater(plugin, runnable, TimeUtil.millisAsTicks(delayMs));
    }

    @Override
    public void sync(Runnable runnable, int delayMs, int periodMs) {
        bukkitScheduler.runTaskTimer(plugin, runnable, TimeUtil.millisAsTicks(delayMs), TimeUtil.millisAsTicks(periodMs));
    }

    @Override
    public void async(Runnable runnable) {
        bukkitScheduler.runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void async(Runnable runnable, int delayMs) {
        bukkitScheduler.runTaskLaterAsynchronously(plugin, runnable, TimeUtil.millisAsTicks(delayMs));
    }

    @Override
    public void async(Runnable runnable, int delayMs, int periodMs) {
        bukkitScheduler.runTaskTimerAsynchronously(plugin, runnable, TimeUtil.millisAsTicks(delayMs), TimeUtil.millisAsTicks(periodMs));
    }
}
