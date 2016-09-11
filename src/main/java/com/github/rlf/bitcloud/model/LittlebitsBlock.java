package com.github.rlf.bitcloud.model;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Directional;

/**
 * Represents a littlebits device in the minecraft world.
 */
public class LittlebitsBlock {
    private final Block block;
    private Device device;

    public LittlebitsBlock(Block block) {
        this.block = block;
    }

    public Location getOutputLocation() {
        BlockState state = block.getState();
        if (state instanceof Directional) {
            return block.getRelative(((Directional)state).getFacing()).getLocation();
        }
        return null;
    }

    public Block getOutputBlock() {
        BlockState state = block.getState();
        if (state instanceof Directional) {
            return block.getRelative(((Directional)state).getFacing());
        }
        return null;
    }

    public Location getInputLocation() {
        BlockState state = block.getState();
        if (state instanceof Directional) {
            return block.getRelative(((Directional)state).getFacing().getOppositeFace()).getLocation();
        }
        return null;
    }
    public Block getInputBlock() {
        BlockState state = block.getState();
        if (state instanceof Directional) {
            return block.getRelative(((Directional)state).getFacing().getOppositeFace());
        }
        return null;
    }

    public Device getDevice() {
        return device;
    }
}
