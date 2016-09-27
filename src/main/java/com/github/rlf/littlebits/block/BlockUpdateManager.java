package com.github.rlf.littlebits.block;

import com.github.rlf.littlebits.async.Scheduler;
import com.github.rlf.littlebits.model.BlockLocation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.RedstoneWire;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Responsible for ensuring any updates to the redstone state, is kept for at least one
 * redstone-tick (2 minecraft, ticks, or 100ms).
 */
public class BlockUpdateManager {
    private final Map<BlockLocation, List<Integer>> outputQueue = new ConcurrentHashMap<>();
    private final Scheduler.Task task;

    public BlockUpdateManager(Scheduler scheduler) {
        task = scheduler.sync(new UpdateBlock(), 100, 100);
    }

    public void shutdown() {
        outputQueue.clear();
        if (task != null) {
            task.cancel();
        }
    }

    public void setCurrent(Block block, int newCurrent) {
        if (block == null || block.getLocation() == null) {
            return;
        }
        BlockLocation location = BlockLocation.wrap(block.getLocation());
        synchronized (outputQueue) {
            if (!outputQueue.containsKey(location)) {
                outputQueue.put(location, new ArrayList<Integer>());
            }
            outputQueue.get(location).add(newCurrent);
        }
    }

    public boolean remove(Block block) {
        if (block == null || block.getLocation() == null) {
            return false;
        }
        BlockLocation location = BlockLocation.wrap(block.getLocation());
        return outputQueue.remove(location) != null;
    }

    private void setNewCurrent(Block outputBlock, int newCurrent) {
        if (outputBlock.getState() instanceof BlockState
                && outputBlock.getState().getData() instanceof RedstoneWire) {
            // TODO: 16/09/2016 - R4zorax: Handle other redstone blocks as well?
            outputBlock.setData((byte) newCurrent);
        }
    }

    private class UpdateBlock extends BukkitRunnable {
        @Override
        public void run() {
            if (outputQueue.isEmpty()) {
                return;
            }
            for (Iterator<Map.Entry<BlockLocation,List<Integer>>> it = outputQueue.entrySet().iterator(); it.hasNext();) {
                Map.Entry<BlockLocation,List<Integer>> entry = it.next();
                Integer newValue = entry.getValue().remove(0);
                if (entry.getValue().isEmpty()) {
                    it.remove();
                }
                Block block = entry.getKey().toLocation().getBlock();
                if (block != null && newValue != null) {
                    setNewCurrent(block, newValue);
                }
            }
        }
    }
}
