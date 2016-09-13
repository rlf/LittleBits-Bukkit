package com.github.rlf.bitcloud.block;

import com.github.rlf.bitcloud.event.DeviceAttached;
import com.github.rlf.bitcloud.event.DeviceDetached;
import com.github.rlf.bitcloud.event.DeviceOutput;
import com.github.rlf.bitcloud.event.EventManager;
import com.github.rlf.bitcloud.model.BlockDB;
import com.github.rlf.bitcloud.model.Device;
import com.github.rlf.bitcloud.model.DeviceDB;
import com.github.rlf.bitcloud.model.LittlebitsBlock;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

/**
 *
 */
public class BlockEvents implements Listener {

    private final BlockDB blockDB;
    private final DeviceDB deviceDB;
    private final EventManager eventManager;

    public BlockEvents(BlockDB blockDB, DeviceDB deviceDB, EventManager eventManager) {
        this.blockDB = blockDB;
        this.deviceDB = deviceDB;
        this.eventManager = eventManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(BlockPlaceEvent e) {
        if (e.isCancelled()
                || e.getBlock() == null
                // TODO: 12/09/2016 - R4zorax: what about the other types (ON & OFF)??
                || e.getBlock().getType() != Material.REDSTONE_COMPARATOR
                || e.getItemInHand() == null
                || e.getItemInHand().getType() != Material.REDSTONE_COMPARATOR
                || e.getItemInHand().getEnchantments().isEmpty()
                )
        {
            return;
        }
        if (LittlebitsBlock.isLittlebitsBlock(e.getItemInHand())) {
            blockDB.add(new LittlebitsBlock(e.getBlock()));
            e.getPlayer().sendMessage(tr("littlebits block placed, right-click to assign device."));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerInteractEvent e) {
        if (e.isCancelled()
                || e.getClickedBlock() == null
                || e.getClickedBlock().getType() != Material.REDSTONE_COMPARATOR
                || e.getAction() != Action.RIGHT_CLICK_BLOCK
                ) {
            return;
        }
        LittlebitsBlock littlebitsBlock = blockDB.getBlock(e.getClickedBlock().getLocation());
        if (littlebitsBlock != null) {
            e.setCancelled(true);
            Device oldDevice = littlebitsBlock.getDevice();
            Device nextDevice = deviceDB.getNextDevice(oldDevice);
            littlebitsBlock.setDevice(nextDevice);
            if (nextDevice != null) {
                e.getPlayer().sendMessage(tr("Changed to device {0}", nextDevice.getLabel()));
            } else {
                e.getPlayer().sendMessage(tr("Disabled this littlebits block."));
            }
            if (oldDevice != null) {
                eventManager.fireEvent(new DeviceDetached(oldDevice, littlebitsBlock));
            }
            if (nextDevice != null) {
                eventManager.fireEvent(new DeviceAttached(nextDevice, littlebitsBlock));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(BlockRedstoneEvent e) {
        Set<LittlebitsBlock> outputs = blockDB.getOutputs(e.getBlock().getLocation());
        if (!outputs.isEmpty()) {
            LittlebitsBlock littlebitsBlock = outputs.iterator().next();
            if (littlebitsBlock != null && littlebitsBlock.getDevice() != null) {
                e.setNewCurrent((int) Math.round(littlebitsBlock.getDevice().getIn()*15));
            }
        }
        Set<LittlebitsBlock> inputs = blockDB.getInputs(e.getBlock().getLocation());
        if (!inputs.isEmpty()) {
            double amplitude = e.getNewCurrent()/15d;
            for (LittlebitsBlock littlebitsBlock : inputs) {
                Device device = littlebitsBlock.getDevice();
                if (device != null && device.getOut() != amplitude) {
                    device.setOut(amplitude);
                    eventManager.fireEvent(new DeviceOutput(device));
                }
            }
        }
    }
}
