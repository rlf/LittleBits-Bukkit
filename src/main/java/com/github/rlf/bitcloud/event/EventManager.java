package com.github.rlf.bitcloud.event;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

/**
 * Abstraction of an event manager
 */
public interface EventManager {
    void fireEvent(Event event);

    void registerListener(Listener listener);
}
