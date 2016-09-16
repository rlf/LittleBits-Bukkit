package com.github.rlf.littlebits.command;

import com.github.rlf.littlebits.model.Account;
import com.github.rlf.littlebits.model.BlockDB;
import com.github.rlf.littlebits.model.BlockLocation;
import com.github.rlf.littlebits.model.Device;
import com.github.rlf.littlebits.model.DeviceComparator;
import com.github.rlf.littlebits.model.DeviceDB;
import com.github.rlf.littlebits.model.LittlebitsBlock;
import dk.lockfuglsang.minecraft.command.AbstractCommand;
import dk.lockfuglsang.minecraft.command.AbstractCommandExecutor;
import dk.lockfuglsang.minecraft.command.DocumentCommand;
import dk.lockfuglsang.minecraft.command.completion.AbstractTabCompleter;
import dk.lockfuglsang.minecraft.command.completion.OnlinePlayerTabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

/**
 * Primary littlebits command
 */
public class LittleBitsCommand extends AbstractCommandExecutor {
    public LittleBitsCommand(JavaPlugin plugin, final DeviceDB deviceDB, final BlockDB blockDB) {
        super("littlebits|bits", "littlebits.use", tr("primary littlebits command"));
        add(new DocumentCommand(plugin, "doc", "littlebits.doc"));
        add(new SaveCommand(deviceDB, blockDB));
        add(new ReloadCommand(plugin));
        add(new AbstractCommand("load", "littlebits.load", tr("loads the databases")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... strings) {
                deviceDB.load();
                blockDB.load();
                commandSender.sendMessage(tr("Databases loaded"));
                return true;
            }
        });

        final List<String> zero_15 = new ArrayList<>();
        for (int i = 0; i <= 15; i++) {
            zero_15.add("" + i);
        }
        // Tab Completers
        addTab("account", new AbstractTabCompleter() {
            @Override
            protected List<String> getTabList(CommandSender commandSender, String s) {
                List<Account> accounts = deviceDB.getAccounts();
                List<String> tabList = new ArrayList<>();
                for (Account account : accounts) {
                    tabList.add(account.getDisplayName());
                }
                return tabList;
            }
        });
        addTab("device", new AbstractTabCompleter() {
            @Override
            protected List<String> getTabList(CommandSender commandSender, String s) {
                List<String> tabList = new ArrayList<>();
                for (Device device : deviceDB.getDevices()) {
                    tabList.add(device.getLabel());
                }
                return tabList;
            }
        });
        addTab("block", new AbstractTabCompleter() {
            @Override
            protected List<String> getTabList(CommandSender commandSender, String s) {
                List<String> list = new ArrayList<>();
                List<LittlebitsBlock> blocks = blockDB.getBlocks();
                for (LittlebitsBlock b : blocks) {
                    list.add(BlockLocation.wrap(b.getLocation()).toString());
                }
                return list;
            }
        });
        addTab("0-15", new AbstractTabCompleter() {
            @Override
            protected List<String> getTabList(CommandSender commandSender, String s) {
                return zero_15;
            }
        });
        addTab("player", new OnlinePlayerTabCompleter());
    }
}
