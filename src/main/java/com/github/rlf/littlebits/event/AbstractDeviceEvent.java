package com.github.rlf.littlebits.event;

import com.github.rlf.littlebits.model.Device;
import org.bukkit.event.Event;

import java.util.Objects;

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

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "device=" + device +
                '}';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (this.getClass() != o.getClass()) {
            return false;
        }
        AbstractDeviceEvent that = (AbstractDeviceEvent) o;
        return Objects.equals(device, that.device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(device);
    }
}
