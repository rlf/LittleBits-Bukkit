package com.github.rlf.bitcloud.event;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 *
 */
public class BlockEvents implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.isCancelled()
                || e.getBlock() == null
                || e.getBlock().getType() != Material.REDSTONE_COMPARATOR
                || e.getItemInHand() == null
                || e.getItemInHand().getType() != Material.REDSTONE_COMPARATOR
                || e.getItemInHand().getEnchantments().isEmpty()
                )
        {
            return;
        }
    }
}
