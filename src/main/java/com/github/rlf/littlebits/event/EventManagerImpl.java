package com.github.rlf.littlebits.event;

import com.github.rlf.littlebits.async.Scheduler;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Adaptor between EventManager and JavaPlugin
 */
public class EventManagerImpl implements EventManager {
    private final JavaPlugin plugin;
    private final Scheduler scheduler;

    public EventManagerImpl(JavaPlugin plugin, Scheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    @Override
    public void fireEvent(final Event event) {
        if (event.isAsynchronous()) {
            scheduler.async(new Runnable() {
                @Override
                public void run() {
                    plugin.getServer().getPluginManager().callEvent(event);
                }
            });
        } else if (scheduler.isSync()) {
            plugin.getServer().getPluginManager().callEvent(event);
        } else {
            scheduler.sync(new Runnable() {
                @Override
                public void run() {
                    plugin.getServer().getPluginManager().callEvent(event);
                }
            });
        }
    }

    @Override
    public void registerListener(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
