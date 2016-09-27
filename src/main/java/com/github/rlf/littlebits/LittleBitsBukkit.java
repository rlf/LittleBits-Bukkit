package com.github.rlf.littlebits;

import com.github.rlf.littlebits.async.Scheduler;
import com.github.rlf.littlebits.async.bukkit.SchedulerBukkit;
import com.github.rlf.littlebits.block.BlockEvents;
import com.github.rlf.littlebits.block.BlockUpdateManager;
import com.github.rlf.littlebits.cloudapi.BukkitCloudAPI;
import com.github.rlf.littlebits.cloudapi.HttpCloudAPI;
import com.github.rlf.littlebits.command.AccountCommand;
import com.github.rlf.littlebits.command.LittleBitsCommand;
import com.github.rlf.littlebits.command.BlockCommand;
import com.github.rlf.littlebits.command.DeviceCommand;
import com.github.rlf.littlebits.event.EventManager;
import com.github.rlf.littlebits.event.EventManagerImpl;
import com.github.rlf.littlebits.model.*;
import dk.lockfuglsang.minecraft.file.FileUtil;
import dk.lockfuglsang.minecraft.po.I18nUtil;
import dk.lockfuglsang.minecraft.yml.YmlConfiguration;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.eventcallbacks.EventCallbacks;

import java.util.Iterator;
import java.util.Locale;

/**
 * Main Bukkit plugin class for the littlebitsBukkit plugin.
 */
public class LittleBitsBukkit extends JavaPlugin {

    private HttpCloudAPI cloudAPI;
    private DeviceDB deviceDB;
    private BlockDB blockDB;
    private BukkitCloudAPI bukkitCloudAPI;
    private BlockUpdateManager blockUpdateManager;

    @Override
    public void onEnable() {
        FileUtil.setDataFolder(getDataFolder());
        I18nUtil.setDataFolder(getDataFolder());
        YmlConfiguration config = getConfig();
        I18nUtil.setLocale(Locale.forLanguageTag(config.getString("language", "en")));

        Scheduler scheduler = new SchedulerBukkit(this, getServer().getScheduler());
        EventManager eventManager = new EventManagerImpl(this, scheduler);
        deviceDB = new FileDeviceDB(eventManager);

        cloudAPI = new HttpCloudAPI(
                config.getString("cloudAPI.baseUrl", null),
                config.getBoolean("cloudAPI.secure", true));
        bukkitCloudAPI = new BukkitCloudAPI(scheduler, cloudAPI, deviceDB, config);
        eventManager.registerListener(bukkitCloudAPI);

        blockDB = new FileBlockDB(deviceDB, eventManager);
        blockUpdateManager = new BlockUpdateManager(scheduler);
        EventCallbacks callbacks = EventCallbacks.of(this);
        eventManager.registerListener(new BlockEvents(blockDB, deviceDB, eventManager, blockUpdateManager, scheduler));
        LittleBitsCommand cmdExecutor = new LittleBitsCommand(this, deviceDB, blockDB);
        cmdExecutor.add(new AccountCommand(deviceDB, eventManager, callbacks));
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
        if (blockUpdateManager != null) {
            blockUpdateManager.shutdown();
        }
        if (bukkitCloudAPI != null) {
            bukkitCloudAPI.shutdown();
        }
        if (deviceDB != null) {
            deviceDB.save();
        }
        if (blockDB != null) {
            blockDB.save();
        }
        HandlerList.unregisterAll(this);
        for (Iterator<Recipe> it = getServer().recipeIterator(); it.hasNext(); ) {
            if (it.next() == LittlebitsBlock.RECIPE) {
                it.remove();
                break;
            }
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
    }

    @Override
    public YmlConfiguration getConfig() {
        return FileUtil.getYmlConfiguration("config.yml");
    }
}
