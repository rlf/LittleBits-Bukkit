package com.github.rlf.bitcloud.event;

import com.github.rlf.bitcloud.model.Device;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event fired when one of the registered devices has been connected.
 */
public abstract class DeviceEvent extends Event {
    private Device device;

    public DeviceEvent(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }
}
