package com.github.rlf.littlebits.event;

import com.github.rlf.littlebits.model.Device;
import org.bukkit.event.HandlerList;

/**
 * An event fired when one of the registered devices receives input (from redstone).
 */
public class DeviceInput extends AbstractDeviceEvent {
    private static final HandlerList handlers = new HandlerList();
    private final int oldValue;

    public DeviceInput(Device device, int oldValue) {
        super(device);
        this.oldValue = oldValue;
    }

    public int getOldValue() {
        return oldValue;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
