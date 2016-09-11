package com.github.rlf.bitcloud;

import com.github.rlf.bitcloud.command.BitCloudCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import sun.net.www.http.HttpClient;

/**
 * Main Bukkit plugin class for the bitCloudBukkit plugin.
 */
public class BitCloud extends JavaPlugin {
    @Override
    public void onEnable() {
        BitCloudEvents eventObject = new BitCloudEvents(getConfig(), this);
        getServer().getPluginManager().registerEvents(eventObject, this);
        getCommand("bitcloud").setExecutor(new BitCloudCommand(this));
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
