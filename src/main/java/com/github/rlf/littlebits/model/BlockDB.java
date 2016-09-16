package com.github.rlf.littlebits.model;

import org.bukkit.Location;

import java.util.List;
import java.util.Set;

/**
 * Responsible for returning LittlebitsBlocks
 */
public interface BlockDB extends AbstractDB {

    /**
     * Returns all littlebits blocks registered
     * @return all littlebits blocks registered
     */
    List<LittlebitsBlock> getBlocks();

    /**
     * Returns all littlebits blocks registered to that device
     * @return all littlebits blocks registered to that device
     */
    List<LittlebitsBlock> getBlocks(Device device);

    /**
     * Returns the associated littlebits blocks for a given location.
     * @param location The block-location.
     * @return The littlebits block at that location, or <code>null</code>
     */
    LittlebitsBlock getBlock(Location location);

    /**
     * Returns littlebits blocks that receive input from this location.
     * @param location The location
     * @return A (possibly empty) list of littlebits blocks.
     */
    Set<LittlebitsBlock> getInputs(Location location);

    /**
     * Returns littlebits blocks that control output for this location.
     * @param location The location
     * @return A (possibly empty) list of littlebits blocks.
     */
    Set<LittlebitsBlock> getOutputs(Location location);

    /**
     * Adds the block to the database.
     * @param block The block to add.
     */
    void add(LittlebitsBlock block);

    void remove(LittlebitsBlock littlebitsBlock);

    void assignDevice(LittlebitsBlock littlebitsBlock, Device nextDevice);
}
