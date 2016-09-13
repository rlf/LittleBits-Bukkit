package com.github.rlf.bitcloud;

import com.github.rlf.bitcloud.block.BlockEvents;
import com.github.rlf.bitcloud.command.BitCloudCommand;
import com.github.rlf.bitcloud.command.SaveCommand;
import com.github.rlf.bitcloud.event.EventManager;
import com.github.rlf.bitcloud.event.EventManagerImpl;
import com.github.rlf.bitcloud.model.BlockDB;
import com.github.rlf.bitcloud.model.DeviceDB;
import com.github.rlf.bitcloud.model.FileBlockDB;
import com.github.rlf.bitcloud.model.FileDeviceDB;
import com.github.rlf.bitcloud.model.LittlebitsBlock;
import dk.lockfuglsang.minecraft.file.FileUtil;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

/**
 * Main Bukkit plugin class for the bitCloudBukkit plugin.
 */
public class BitCloud extends JavaPlugin {

    private DeviceDB deviceDB;
    private BlockDB blockDB;

    @Override
    public void onEnable() {
        FileUtil.setDataFolder(getDataFolder());
        EventManager eventManager = new EventManagerImpl(this);
        deviceDB = new FileDeviceDB();
        blockDB = new FileBlockDB(deviceDB, eventManager);
        eventManager.registerListener(new BlockEvents(blockDB, deviceDB, eventManager));
        BitCloudCommand cmdExecutor = new BitCloudCommand(this);
        cmdExecutor.add(new SaveCommand(deviceDB, blockDB));
        getCommand("bitcloud").setExecutor(cmdExecutor);

        getServer().addRecipe(LittlebitsBlock.RECIPE);
    }

    @Override
    public void onDisable() {
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
}
