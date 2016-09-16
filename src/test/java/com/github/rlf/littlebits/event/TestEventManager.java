package com.github.rlf.littlebits.event;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenience EventManager for testing
 */
public class TestEventManager implements EventManager {
    private final List<Event> events = new ArrayList<>();

    @Override
    public void fireEvent(Event event) {
        events.add(event);
    }

    @Override
    public void registerListener(Listener listener) {
        // TODO: 14/09/2016 - R4zorax: actually support listening?
    }

    public List<Event> getEvents() {
        return events;
    }

    public void clear() {
        events.clear();
    }
}
