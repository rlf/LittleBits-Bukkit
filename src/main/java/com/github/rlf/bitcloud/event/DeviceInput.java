package com.github.rlf.bitcloud.event;

import com.github.rlf.bitcloud.model.Device;
import org.bukkit.event.HandlerList;

/**
 * An event fired when one of the registered devices receives input (from redstone).
 */
public class DeviceInput extends DeviceEvent {
    private static final HandlerList handlers = new HandlerList();
    private final double amplitude;

    public DeviceInput(Device device, double amplitude) {
        super(device);
        this.amplitude = amplitude;
    }

    /**
     * From 0 to 1.0 both inclusive.
     */
    public double getAmplitude() {
        return amplitude;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
