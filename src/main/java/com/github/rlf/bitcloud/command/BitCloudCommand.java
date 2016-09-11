package com.github.rlf.bitcloud.command;

import dk.lockfuglsang.minecraft.command.AbstractCommandExecutor;
import dk.lockfuglsang.minecraft.command.DocumentCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Primary bitCloud command
 */
public class BitCloudCommand extends AbstractCommandExecutor {
    public BitCloudCommand(JavaPlugin plugin) {
        super("bitcloud|bc", "bitcloud.use", "");
        add(new DocumentCommand(plugin, "doc", "bitcloud.doc"));
    }
}
