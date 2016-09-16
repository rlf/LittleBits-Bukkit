package com.github.rlf.littlebits.command;

import dk.lockfuglsang.minecraft.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

public class ReloadCommand extends AbstractCommand {
    private final JavaPlugin plugin;

    public ReloadCommand(JavaPlugin plugin) {
        super("reload", "littlebits.reload", tr("saves and reloads the plugin configuration"));
        this.plugin = plugin;
    }
    @Override
    public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... strings) {
        plugin.onDisable();
        plugin.onEnable();
        commandSender.sendMessage(tr("Plugin has been reloaded!"));
        return true;
    }
}
