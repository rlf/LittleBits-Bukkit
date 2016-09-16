package com.github.rlf.littlebits;

import com.github.rlf.littlebits.async.Scheduler;
import com.github.rlf.littlebits.async.bukkit.SchedulerBukkit;
import com.github.rlf.littlebits.block.BlockEvents;
import com.github.rlf.littlebits.cloudapi.BukkitCloudAPI;
import com.github.rlf.littlebits.cloudapi.HttpCloudAPI;
import com.github.rlf.littlebits.command.AccountCommand;
import com.github.rlf.littlebits.command.LittleBitsCommand;
import com.github.rlf.littlebits.command.BlockCommand;
import com.github.rlf.littlebits.command.DeviceCommand;
import com.github.rlf.littlebits.command.ReloadCommand;
import com.github.rlf.littlebits.command.SaveCommand;
import com.github.rlf.littlebits.event.EventManager;
import com.github.rlf.littlebits.event.EventManagerImpl;
import com.github.rlf.littlebits.model.*;
import dk.lockfuglsang.minecraft.file.FileUtil;
import dk.lockfuglsang.minecraft.yml.YmlConfiguration;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

/**
 * Main Bukkit plugin class for the littlebitsBukkit plugin.
 */
public class LittleBitsBukkit extends JavaPlugin {

    private HttpCloudAPI cloudAPI;
    private DeviceDB deviceDB;
    private BlockDB blockDB;
    private BukkitCloudAPI bukkitCloudAPI;

    @Override
    public void onEnable() {
        FileUtil.setDataFolder(getDataFolder());
        Scheduler scheduler = new SchedulerBukkit(this, getServer().getScheduler());
        EventManager eventManager = new EventManagerImpl(this, scheduler);
        deviceDB = new FileDeviceDB(eventManager);

        YmlConfiguration config = getConfig();
        cloudAPI = new HttpCloudAPI(config.getString("cloudAPI.baseUrl", null));
        bukkitCloudAPI = new BukkitCloudAPI(scheduler, cloudAPI, deviceDB, config);
        eventManager.registerListener(bukkitCloudAPI);

        blockDB = new FileBlockDB(deviceDB, eventManager);
        eventManager.registerListener(new BlockEvents(blockDB, deviceDB, eventManager));
        LittleBitsCommand cmdExecutor = new LittleBitsCommand(this, deviceDB, blockDB);
        cmdExecutor.add(new AccountCommand(deviceDB, eventManager));
        cmdExecutor.add(new BlockCommand(blockDB, deviceDB));
        cmdExecutor.add(new DeviceCommand(deviceDB, eventManager));
        PluginCommand pluginCmd = getCommand("littlebits");
        pluginCmd.setExecutor(cmdExecutor);
        pluginCmd.setTabCompleter(cmdExecutor);

        getServer().addRecipe(LittlebitsBlock.RECIPE);
        deviceDB.load();
        blockDB.load();
    }

    @Override
    public void onDisable() {
        bukkitCloudAPI.shutdown();
        deviceDB.save();
        blockDB.save();
        HandlerList.unregisterAll(this);
        for (Iterator<Recipe> it = getServer().recipeIterator(); it.hasNext(); ) {
            if (it.next() == LittlebitsBlock.RECIPE) {
                it.remove();
                break;
            }
        }
    }

    @Override
    public YmlConfiguration getConfig() {
        return FileUtil.getYmlConfiguration("config.yml");
    }
}
