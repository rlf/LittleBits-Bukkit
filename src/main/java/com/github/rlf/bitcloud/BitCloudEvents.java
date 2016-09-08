package com.github.rlf.bitcloud;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

import static java.lang.Math.random;

/**
 * Simpel Listener der fyrer en ildkugle afsted når en spiller klikker med en pil.
 */
public class BitCloudEvents implements Listener {

    private final JavaPlugin plugin;
    private int fireball_yield;
    private double speed;

    public BitCloudEvents(FileConfiguration config, JavaPlugin plugin) {
        this.plugin = plugin;
        // Default værdien er 3, men kan overstyres i config.yml, hvis man vil.
        fireball_yield = config.getInt("fireball.yield", 3);
        speed = config.getDouble("fireball.speed", 3d);
    }

    /**
     * @see @link https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/player/PlayerInteractEvent.html
     */
    @EventHandler
    public void onFireGesture(PlayerInteractEvent e) {
        // BEMÆRK: e.isCancelled() er true, hvis eventen ikke gør noget i Vanilla
        //         f.eks. hvis man slår ud i luften
        // || = eller, | IKKE eller (bitvis-eller)
        // && = og
        if (e.getPlayer() == null || e.getHand() != EquipmentSlot.HAND || e.getItem() == null) {
            // Lad være med at spilde tid på det så
            return;
        }
        // Spilleren brugte sin HAND (ikke off-hand).
        Player player = e.getPlayer();
        ItemStack itemInHand = e.getItem();
        if (itemInHand.getType() == Material.ARROW) {
            Location eyeLocation = player.getEyeLocation();
            Fireball fireball = e.getPlayer().getWorld().spawn(eyeLocation, Fireball.class);
            fireball.setBounce(true);
            fireball.setIsIncendiary(true);
            fireball.setCustomName(player.getDisplayName() + "'s ildkugle");
            fireball.setCustomNameVisible(true);
            fireball.setShooter(player);
            fireball.setYield(fireball_yield);
            fireball.setMetadata("spawnSheep", new FixedMetadataValue(plugin, Boolean.TRUE));
            fireball.setVelocity(new Vector(0, -0.5, 0));
            e.setCancelled(true); // Undlad at "slå med pilen", det klarer ildkuglen.
        } else if (itemInHand.getType() == Material.WOOL) {
            Location eyeLocation = player.getEyeLocation();
            Sheep sheep = e.getPlayer().getWorld().spawn(eyeLocation, Sheep.class);
            sheep.setCustomName(player.getDisplayName() + "'s får");
            sheep.setVelocity(eyeLocation.getDirection().multiply(speed));
            e.setCancelled(true); // Undlad at "slå med pilen", det klarer ildkuglen.
        }
    }

    @EventHandler
    public void onFireballExplode(EntityExplodeEvent e) {
        if (e.getEntity() instanceof Fireball && e.getEntity().getMetadata("spawnSheep") != null) {
            e.setCancelled(true);
            for (int i = 0; i < 8; i++) {
                Location l = e.getLocation().add(random() * fireball_yield, 0, random() * fireball_yield);
                Sheep sheep = (Sheep) e.getLocation().getWorld().spawnEntity(l, EntityType.SHEEP);
                sheep.setFireTicks((int) Math.round(random()*5 + 5));
            }
        }
    }

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Snowball) {
            Snowball snowball = (Snowball) e.getEntity();
            Location loc = snowball.getLocation();
            List<Block> blocks = Arrays.asList(
                    loc.getBlock(),
                    loc.getBlock().getRelative(BlockFace.UP)
            );
            for (Block block : blocks) {
                if (block.getType() == Material.FIRE) {
                    block.setType(Material.AIR);
                }
            }
        }
    }
}
