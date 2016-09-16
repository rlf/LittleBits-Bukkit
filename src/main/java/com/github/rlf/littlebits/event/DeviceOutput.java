package com.github.rlf.littlebits.event;

import com.github.rlf.littlebits.model.Device;
import org.bukkit.event.HandlerList;

/**
 * An event fired when one of the registered devices generates an output (from littlebits).
 */
public class DeviceOutput extends AbstractDeviceEvent {
    private static final HandlerList handlers = new HandlerList();

    public DeviceOutput(Device device, int oldValue) {
        super(device);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
