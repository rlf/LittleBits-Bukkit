package com.github.rlf.littlebits.event;

import com.github.rlf.littlebits.model.Device;
import com.github.rlf.littlebits.model.LogEntry;
import org.bukkit.event.HandlerList;

/**
 * An event fired when one of the registered devices has been connected.
 */
public class DeviceLogEvent extends AbstractDeviceEvent {
    private static final HandlerList handlers = new HandlerList();
    private LogEntry entry;

    public DeviceLogEvent(Device device, LogEntry entry) {
        super(device);
        this.entry = entry;
    }

    public LogEntry getEntry() {
        return entry;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
