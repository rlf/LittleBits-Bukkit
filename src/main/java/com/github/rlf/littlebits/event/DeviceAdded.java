package com.github.rlf.littlebits.event;

import com.github.rlf.littlebits.model.Device;
import org.bukkit.event.HandlerList;

/**
 * An event fired when one of the registered devices has been connected.
 */
public class DeviceAdded extends AbstractDeviceEvent {
    private static final HandlerList handlers = new HandlerList();

    public DeviceAdded(Device device) {
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
