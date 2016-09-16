package com.github.rlf.littlebits.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

/**
 * A simple immutable object representing the bukkit location.
 */
public class BlockLocation {
    private final String world;
    private final int x;
    private final int y;
    private final int z;

    public BlockLocation(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockLocation)) return false;
        BlockLocation that = (BlockLocation) o;
        return x == that.x &&
                y == that.y &&
                z == that.z &&
                Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }

    @Override
    public String toString() {
        return world + ":" + x + "," + y + "," + z;
    }

    /**
     * a key that can be used in yml
     */
    public String toKey() {
        return world + " " + x + " " + y + " " + z;
    }

    public static BlockLocation wrap(String s) {
        String[] parts = s.split("[ :,]");
        try {
            if (parts.length == 4) {
                return new BlockLocation(parts[0],
                        Integer.parseInt(parts[1], 10),
                        Integer.parseInt(parts[2], 10),
                        Integer.parseInt(parts[3], 10));
            }
        } catch (NumberFormatException e) {
            // Ignore - just return null
        }
        return null;
    }

    public static BlockLocation wrap(Location loc) {
        if (loc == null) {
            return null;
        }
        return new BlockLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
}
