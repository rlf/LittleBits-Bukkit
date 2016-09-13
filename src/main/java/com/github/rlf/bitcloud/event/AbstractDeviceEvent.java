package com.github.rlf.bitcloud.event;

import com.github.rlf.bitcloud.model.Device;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event fired when one of the registered devices has been connected.
 */
public abstract class AbstractDeviceEvent extends Event {
    private Device device;

    public AbstractDeviceEvent(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }
}
