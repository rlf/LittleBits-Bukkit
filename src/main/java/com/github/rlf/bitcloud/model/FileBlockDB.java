package com.github.rlf.bitcloud.model;

import com.github.rlf.bitcloud.event.DeviceAttached;
import com.github.rlf.bitcloud.event.EventManager;
import dk.lockfuglsang.minecraft.file.FileUtil;
import dk.lockfuglsang.minecraft.yml.YmlConfiguration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Database holding all the registered littlebits blocks.
 */
public class FileBlockDB implements BlockDB {
    private static final Logger log = Logger.getLogger(FileBlockDB.class.getName());

    private final DeviceDB deviceDB;
    private final EventManager eventManager;
    private YmlConfiguration config;
    private boolean dirty = false;

    private final Map<BlockLocation, LittlebitsBlock> blocks = new ConcurrentHashMap<>();
    private final Map<BlockLocation, Set<LittlebitsBlock>> inputs = new ConcurrentHashMap<>();
    private final Map<BlockLocation, Set<LittlebitsBlock>> outputs = new ConcurrentHashMap<>();

    public FileBlockDB(DeviceDB deviceDB, EventManager eventManager) {
        this.deviceDB = deviceDB;
        this.eventManager = eventManager;
        load();
    }

    @Override
    public void load() {
        blocks.clear();
        inputs.clear();
        outputs.clear();
        config = FileUtil.getYmlConfiguration("blocks.yml");
        ConfigurationSection sec = config.getConfigurationSection("blocks");
        if (sec != null) {
            for (String key : sec.getKeys(false)) {
                BlockLocation bl = BlockLocation.wrap(key);
                if (bl != null) {
                    Block block = bl.toLocation().getBlock();
                    if (LittlebitsBlock.isLittlebitsBlockType(block)) {
                        LittlebitsBlock littlebitsBlock = new LittlebitsBlock(block);
                        String deviceId = sec.getString(key, null);
                        if (deviceId != null) {
                            Device device = deviceDB.getDevice(deviceId);
                            littlebitsBlock.setDevice(device);
                        }
                        addToCache(littlebitsBlock);
                    }
                }
            }
        }
        dirty = false;
    }

    @Override
    public void save() {
        if (dirty) {
            // TODO: 12/09/2016 - R4zorax: Spin off the Server Thread
            try {
                config.save(FileUtil.getConfigFile("blocks.yml"));
            } catch (IOException e) {
                log.info("Unable to save file: " + e);
            }
        }
    }

    private void addToMap(Map<BlockLocation,Set<LittlebitsBlock>> map, Location loc, LittlebitsBlock littlebitsBlock) {
        BlockLocation location = BlockLocation.wrap(loc);
        synchronized (map) {
            if (!map.containsKey(location)) {
                map.put(location, new HashSet<LittlebitsBlock>());
            }
            map.get(location).add(littlebitsBlock);
        }
    }

    @Override
    public List<LittlebitsBlock> getBlocks() {
        return Collections.unmodifiableList(new ArrayList<>(blocks.values()));
    }

    @Override
    public LittlebitsBlock getBlock(Location location) {
        return blocks.get(BlockLocation.wrap(location));
    }

    @Override
    public List<LittlebitsBlock> getAssociatedBlocks(Location location) {
        List<LittlebitsBlock> result = new ArrayList<>();
        BlockLocation loc = BlockLocation.wrap(location);
        LittlebitsBlock block = blocks.get(loc);
        if (block != null) {
            result.add(block);
        }
        Set<LittlebitsBlock> set = inputs.get(loc);
        if (set != null) {
            result.addAll(set);
        }
        set = outputs.get(loc);
        if (set != null) {
            result.addAll(set);
        }
        return result;
    }

    @Override
    public Set<LittlebitsBlock> getInputs(Location location) {
        BlockLocation loc = BlockLocation.wrap(location);
        return inputs.containsKey(loc) ? Collections.unmodifiableSet(inputs.get(loc)) : Collections.<LittlebitsBlock>emptySet();
    }

    @Override
    public Set<LittlebitsBlock> getOutputs(Location location) {
        BlockLocation loc = BlockLocation.wrap(location);
        return outputs.containsKey(loc) ? Collections.unmodifiableSet(outputs.get(loc)) : Collections.<LittlebitsBlock>emptySet();
    }

    @Override
    public void add(LittlebitsBlock block) {
        addToCache(block);
        Device device = block.getDevice();
        config.set("blocks." + BlockLocation.wrap(block.getLocation()).toString(), device != null ? device.getId() : "");
        dirty = true;
    }

    private void addToCache(LittlebitsBlock block) {
        Location loc = block.getLocation();
        BlockLocation bl = BlockLocation.wrap(loc);
        blocks.put(bl, block);
        addToMap(inputs, block.getInput(), block);
        addToMap(outputs, block.getOutput(), block);
        if (block.getDevice() != null) {
            eventManager.fireEvent(new DeviceAttached(block.getDevice(), block));
        }
    }
}
