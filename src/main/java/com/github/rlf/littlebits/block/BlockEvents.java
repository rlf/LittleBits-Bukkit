package com.github.rlf.littlebits.block;

import com.github.rlf.littlebits.event.DeviceAttached;
import com.github.rlf.littlebits.event.DeviceInput;
import com.github.rlf.littlebits.event.EventManager;
import com.github.rlf.littlebits.model.BlockDB;
import com.github.rlf.littlebits.model.BlockLocation;
import com.github.rlf.littlebits.model.Device;
import com.github.rlf.littlebits.model.DeviceDB;
import com.github.rlf.littlebits.model.LittlebitsBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Redstone;
import org.bukkit.material.RedstoneWire;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

/**
 * Responsible for reading (and writing) to and from the actual minecraft blocks.
 */
public class BlockEvents implements Listener {

    private final BlockDB blockDB;
    private final DeviceDB deviceDB;
    private final EventManager eventManager;
    private final BlockUpdateManager blockUpdateManager;
    private final Map<BlockLocation, List<Integer>> outputQueue = new ConcurrentHashMap<>();

    public BlockEvents(BlockDB blockDB, DeviceDB deviceDB, EventManager eventManager, BlockUpdateManager blockUpdateManager) {
        this.blockDB = blockDB;
        this.deviceDB = deviceDB;
        this.eventManager = eventManager;
        this.blockUpdateManager = blockUpdateManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLittlebitsBlockPlace(BlockPlaceEvent e) {
        if (e.isCancelled()
                || e.getBlock() == null
                // TODO: 12/09/2016 - R4zorax: what about the other types (ON & OFF)??
                || e.getBlock().getType() != Material.REDSTONE_COMPARATOR_OFF
                || e.getItemInHand() == null
                || e.getItemInHand().getType() != Material.REDSTONE_COMPARATOR
                || e.getItemInHand().getEnchantments().isEmpty()
                ) {
            return;
        }
        if (LittlebitsBlock.isLittlebitsItemStack(e.getItemInHand())) {
            if (e.getPlayer().hasPermission("littlebits.block.place")) {
                blockDB.add(new LittlebitsBlock(e.getBlock()));
                e.getPlayer().sendMessage(tr("littlebits block placed, right-click to assign device."));
            } else {
                e.setCancelled(true);
                e.getPlayer().sendMessage(tr("You are not allowed to place littleBits (littlebits.block.place)"));
            }
        }
    }
    @EventHandler
    public void onRedstonePlace(BlockPlaceEvent e) {
        if (e.isCancelled()
                || e.getBlock() == null
                || e.getBlock().getType() == null
                || e.getBlock().getState() == null
                || !(e.getBlock().getState().getData() instanceof Redstone)
                ) {
            return;
        }
        Set<LittlebitsBlock> inputs = blockDB.getInputs(e.getBlock().getLocation());
        for (LittlebitsBlock bits : inputs) {
            int blockPower = e.getBlock().getBlockPower();
            deviceDB.setOutput(bits.getDevice(), blockPower);
        }
        eventManager.fireEvent(new BlockRedstoneEvent(e.getBlock(), 1, 0));
    }

    @EventHandler
    public void on(BlockBreakEvent e) {
        if (e.isCancelled() || e.getBlock() == null) {
            return;
        }
        LittlebitsBlock littlebitsBlock = blockDB.getBlock(e.getBlock().getLocation());
        if (littlebitsBlock != null) {
            if (e.getPlayer().hasPermission("littlebits.block.break")) {
                blockDB.remove(littlebitsBlock);
                blockUpdateManager.remove(e.getBlock());
            } else {
                e.setCancelled(true);
                e.getPlayer().sendMessage(tr("You are not allowed to break littleBits (littlebits.block.break)"));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLeftClickLittleBit(PlayerInteractEvent e) {
        if (e.isCancelled()
                || e.getClickedBlock() == null
                || e.getPlayer() == null
                || (e.getClickedBlock().getType() != Material.REDSTONE_COMPARATOR_OFF && e.getClickedBlock().getType() != Material.REDSTONE_COMPARATOR_ON)
                || e.getAction() != Action.LEFT_CLICK_BLOCK
                || !LittlebitsBlock.isLittlebitsItemStack(e.getItem())
                ) {
            return;
        }
        LittlebitsBlock littlebitsBlock = blockDB.getBlock(e.getClickedBlock().getLocation());
        if (littlebitsBlock != null) {
            if (e.getPlayer().hasPermission("littlebits.block.info")) {
                e.setCancelled(true);
                Device device = littlebitsBlock.getDevice();
                e.getPlayer().sendMessage(tr("Device {0}", device != null ? device.toString() : tr("-none-")));
            } else {
                e.getPlayer().sendMessage(tr("You are not allowed to see littleBits info (littlebits.block.info)"));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onRightClickLittleBit(PlayerInteractEvent e) {
        if (e.isCancelled()
                || e.getClickedBlock() == null
                || (e.getClickedBlock().getType() != Material.REDSTONE_COMPARATOR_OFF && e.getClickedBlock().getType() != Material.REDSTONE_COMPARATOR_ON)
                || e.getAction() != Action.RIGHT_CLICK_BLOCK
                ) {
            return;
        }
        LittlebitsBlock littlebitsBlock = blockDB.getBlock(e.getClickedBlock().getLocation());
        if (littlebitsBlock != null) {
            e.setCancelled(true);
            if (e.getPlayer().hasPermission("littlebits.block.assign")) {
                Device nextDevice = deviceDB.getNextDevice(littlebitsBlock.getDevice());
                blockDB.assignDevice(littlebitsBlock, nextDevice);
                if (nextDevice != null) {
                    e.getPlayer().sendMessage(tr("Changed to device {0}", nextDevice.getLabel()));
                } else {
                    e.getPlayer().sendMessage(tr("Disabled this littlebits block."));
                }
            } else {
                e.getPlayer().sendMessage(tr("You are not allowed to change littleBits (littlebits.block.assign)"));
            }
        }
    }

    @EventHandler
    public void on(DeviceAttached e) {
        LittlebitsBlock littlesbitsBlock = e.getLittlesbitsBlock();
        Block outputBlock = littlesbitsBlock.getOutput().getBlock();
        if (outputBlock != null && outputBlock.getState() instanceof Redstone) {
            eventManager.fireEvent(new BlockRedstoneEvent(outputBlock, 0, littlesbitsBlock.getDevice().getOut()));
        }
    }

    @EventHandler
    public void on(DeviceInput e) {
        List<LittlebitsBlock> blocks = blockDB.getBlocks(e.getDevice());
        int newCurrent = e.getDevice().getIn();
        for (LittlebitsBlock block : blocks) {
            Block outputBlock = block.getOutputBlock();
            blockUpdateManager.setCurrent(outputBlock, newCurrent);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(BlockRedstoneEvent e) {
        Set<LittlebitsBlock> outputs = blockDB.getOutputs(e.getBlock().getLocation());
        if (!outputs.isEmpty()) {
            LittlebitsBlock littlebitsBlock = outputs.iterator().next();
            if (littlebitsBlock != null && littlebitsBlock.getDevice() != null) {
                int newCurrent = littlebitsBlock.getDevice().getIn();
                e.setNewCurrent(newCurrent);
            }
        }
        Set<LittlebitsBlock> inputs = blockDB.getInputs(e.getBlock().getLocation());
        if (!inputs.isEmpty()) {
            for (LittlebitsBlock littlebitsBlock : inputs) {
                int amplitude = littlebitsBlock.getInputPower();
                Device device = littlebitsBlock.getDevice();
                if (device != null && device.getOut() != amplitude) {
                    deviceDB.setOutput(device, amplitude);
                }
            }
        }
    }
}
