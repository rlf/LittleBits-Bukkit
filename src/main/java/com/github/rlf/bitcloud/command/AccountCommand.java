package com.github.rlf.bitcloud.command;

import com.github.rlf.bitcloud.model.Account;
import com.github.rlf.bitcloud.model.Device;
import com.github.rlf.bitcloud.model.DeviceDB;
import dk.lockfuglsang.minecraft.command.AbstractCommand;
import dk.lockfuglsang.minecraft.command.CompositeCommand;
import dk.lockfuglsang.minecraft.command.completion.AbstractTabCompleter;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

/**
 * Responsible for editing account information.
 */
public class AccountCommand extends CompositeCommand {

    public AccountCommand(final DeviceDB deviceDB) {
        super("account|acc", "bitcloud.account", tr("handle littlebit accounts"));
        add(new AbstractCommand("add", "bitcloud.account.add", "account", tr("add an account")) {
            @Override
            public boolean execute(CommandSender commandSender, String alias, Map<String, Object> map, String... args) {
                if (args.length == 1) {
                    Account account = deviceDB.addAccount(args[0]);
                    commandSender.sendMessage(tr("Added account with token {0}", account.getToken()));
                    return true;
                }
                return false;
            }
        });
        add(new AbstractCommand("remove", "bitcloud.account.remove", "account", tr("remove an account")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                if (args.length == 1) {
                    if (deviceDB.removeAccount(args[0])) {
                        commandSender.sendMessage(tr("removed account {0}", args[0]));
                    } else {
                        commandSender.sendMessage(tr("no account with id {0} was found", args[0]));
                    }
                    return true;
                }
                return false;
            }
        });
        add(new AbstractCommand("list|ls", "bitcloud.account.list", tr("list all current accounts")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... strings) {
                String msg = tr("All accounts:") + "\n";
                for (Account account : deviceDB.getAccounts()) {
                    msg += tr("  Account: {0}\n", account.getToken());
                    for (Device device : account.getDevices()) {
                        msg += tr("    - {0} ({1})\n", device.getLabel(), device.getId());
                    }
                }
                commandSender.sendMessage(msg.split("\n"));
                return true;
            }
        });
        addTab("account", new AbstractTabCompleter() {
            @Override
            protected List<String> getTabList(CommandSender commandSender, String s) {
                List<Account> accounts = deviceDB.getAccounts();
                List<String> tabList = new ArrayList<>();
                for (Account account : accounts) {
                    tabList.add(account.getToken());
                }
                return tabList;
            }
        });
    }

}
