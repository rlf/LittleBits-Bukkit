package com.github.rlf.bitcloud.event;

import com.github.rlf.bitcloud.model.Device;
import org.bukkit.event.HandlerList;

/**
 * An event fired when one of the registered devices has been connected.
 */
public class DeviceDisconnected extends AbstractDeviceEvent {
    private static final HandlerList handlers = new HandlerList();

    public DeviceDisconnected(Device device) {
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
