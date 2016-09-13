package com.github.rlf.bitcloud.event;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by R4zorax on 12/09/2016.
 */
public class EventManagerImpl implements EventManager {
    private final JavaPlugin plugin;

    public EventManagerImpl(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void fireEvent(Event event) {
        plugin.getServer().getPluginManager().callEvent(event);
    }

    @Override
    public void registerListener(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
