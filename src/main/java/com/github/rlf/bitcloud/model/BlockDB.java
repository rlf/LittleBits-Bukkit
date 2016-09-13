package com.github.rlf.bitcloud.model;

import org.bukkit.Location;

import java.util.List;
import java.util.Set;

/**
 * Responsible for returning LittlebitsBlocks
 */
public interface BlockDB extends AbstractDB {

    /**
     * Returns the associated littlebits blocks for a given location.
     * @param location The block-location.
     * @return The littlebits block at that location, or <code>null</code>
     */
    LittlebitsBlock getBlock(Location location);

    /**
     * Returns a list of associated littlebits blocks for a given location.
     * @param location The block, input or output location.
     * @return A list of littlebits blocks associated to the location (may be empty, not null).
     */
    List<LittlebitsBlock> getAssociatedBlocks(Location location);

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
}
