package com.github.rlf.littlebits.model;

import com.github.rlf.littlebits.event.DeviceAdded;
import com.github.rlf.littlebits.event.DeviceAttached;
import com.github.rlf.littlebits.event.DeviceDetached;
import com.github.rlf.littlebits.event.DeviceInput;
import com.github.rlf.littlebits.event.DeviceUpdated;
import com.github.rlf.littlebits.event.EventManager;
import dk.lockfuglsang.minecraft.file.FileUtil;
import dk.lockfuglsang.minecraft.yml.YmlConfiguration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

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
public class FileBlockDB implements BlockDB, Listener {
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
        eventManager.registerListener(this);
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
        if (!dirty) {
            return;
        }
        // TODO: 12/09/2016 - R4zorax: Spin off the Server Thread
        try {
            config.set("blocks", null);
            ConfigurationSection blockSection = config.createSection("blocks");
            for (BlockLocation loc : blocks.keySet()) {
                Device device = blocks.get(loc).getDevice();
                blockSection.set(loc.toKey(), device != null ? device.getId() : "");
            }
            config.save(FileUtil.getConfigFile("blocks.yml"));
        } catch (IOException e) {
            log.info("Unable to save file: " + e);
        }
    }

    private void addToMap(Map<BlockLocation, Set<LittlebitsBlock>> map, Location loc, LittlebitsBlock littlebitsBlock) {
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
    public List<LittlebitsBlock> getBlocks(Device device) {
        List<LittlebitsBlock> result = new ArrayList<>();
        for (LittlebitsBlock block : blocks.values()) {
            if (device == null && block.getDevice() == null) {
                result.add(block);
            } else if (device != null && device.equals(block.getDevice())) {
                result.add(block);
            }
        }
        return result;
    }

    @Override
    public LittlebitsBlock getBlock(Location location) {
        return blocks.get(BlockLocation.wrap(location));
    }

    @Override
    public Set<LittlebitsBlock> getInputs(Location location) {
        BlockLocation loc = BlockLocation.wrap(location);
        Set<LittlebitsBlock> input = new HashSet<>();
        if (inputs.containsKey(loc)) {
            input.addAll(inputs.get(loc));
        }
        if (blocks.containsKey(loc)) {
            input.add(blocks.get(loc));
        }
        return input;
    }

    @Override
    public Set<LittlebitsBlock> getOutputs(Location location) {
        BlockLocation loc = BlockLocation.wrap(location);
        return outputs.containsKey(loc) ? Collections.unmodifiableSet(outputs.get(loc)) : Collections.<LittlebitsBlock>emptySet();
    }

    @Override
    public void add(LittlebitsBlock block) {
        addToCache(block);
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

    @Override
    public void remove(LittlebitsBlock littlebitsBlock) {
        removeFromCache(littlebitsBlock);
        dirty = true;
    }

    @Override
    public void assignDevice(LittlebitsBlock littlebitsBlock, Device nextDevice) {
        Device oldDevice = littlebitsBlock.getDevice();
        littlebitsBlock.setDevice(nextDevice);
        if (oldDevice != null) {
            eventManager.fireEvent(new DeviceDetached(oldDevice, littlebitsBlock));
        }
        if (nextDevice != null) {
            eventManager.fireEvent(new DeviceAttached(nextDevice, littlebitsBlock));
        }
    }

    private void removeFromCache(LittlebitsBlock block) {
        Location loc = block.getLocation();
        BlockLocation bl = BlockLocation.wrap(loc);
        blocks.remove(bl);
        removeFromMap(inputs, block.getInput(), block);
        removeFromMap(outputs, block.getOutput(), block);
        if (block.getDevice() != null) {
            eventManager.fireEvent(new DeviceDetached(block.getDevice(), block));
        }
    }

    private void removeFromMap(Map<BlockLocation, Set<LittlebitsBlock>> map, Location loc, LittlebitsBlock block) {
        BlockLocation location = BlockLocation.wrap(loc);
        synchronized (map) {
            Set<LittlebitsBlock> blocks = map.get(location);
            blocks.remove(block);
            if (blocks.isEmpty()) {
                map.remove(location);
            }
        }
    }

    // Listener
    @EventHandler
    public void on(DeviceUpdated e) {
        triggerRedstone(e.getDevice());
    }

    @EventHandler
    public void on(DeviceAdded e) {
        triggerRedstone(e.getDevice());
    }

    @EventHandler
    public void on(DeviceAttached e) {
        triggerRedstone(e.getDevice());
        dirty = true;
    }

    @EventHandler
    public void on(DeviceDetached e) {
        dirty = true;
    }

    /**
     * Triggers the events that lead to device updating.
     */
    private void triggerRedstone(Device device) {
        List<LittlebitsBlock> blocks = getBlocks(device);
        for (LittlebitsBlock block : blocks) {
            Block inputBlock = block.getInputBlock();
            eventManager.fireEvent(new BlockRedstoneEvent(inputBlock, 0, inputBlock.getBlockPower()));
            eventManager.fireEvent(new BlockRedstoneEvent(block.getOutputBlock(), 0, device.getIn()));
        }
    }
}
