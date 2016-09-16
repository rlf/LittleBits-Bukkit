package com.github.rlf.littlebits.event;

import com.github.rlf.littlebits.model.Device;
import com.github.rlf.littlebits.model.LittlebitsBlock;
import org.bukkit.event.HandlerList;

/**
 * An event fired when a Littlebits device is detached from a block.
 */
public class DeviceDetached extends AbstractDeviceEvent {
    private static final HandlerList handlers = new HandlerList();
    private final LittlebitsBlock block;

    public DeviceDetached(Device device, LittlebitsBlock block) {
        super(device);
        this.block = block;
    }

    public LittlebitsBlock getBlock() {
        return block;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
