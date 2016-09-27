package com.github.rlf.littlebits.command;

import com.github.rlf.littlebits.event.AccountAdded;
import com.github.rlf.littlebits.event.AccountRemoved;
import com.github.rlf.littlebits.event.AccountUpdated;
import com.github.rlf.littlebits.event.EventManager;
import com.github.rlf.littlebits.model.Account;
import com.github.rlf.littlebits.model.Device;
import com.github.rlf.littlebits.model.DeviceDB;
import dk.lockfuglsang.minecraft.command.AbstractCommand;
import dk.lockfuglsang.minecraft.command.CompositeCommand;
import org.bukkit.command.CommandSender;
import org.inventivetalent.eventcallbacks.EventCallback;
import org.inventivetalent.eventcallbacks.EventCallbacks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

/**
 * Responsible for editing account information.
 */
public class AccountCommand extends CompositeCommand {

    private final DeviceDB deviceDB;

    public AccountCommand(final DeviceDB deviceDB,
                          final EventManager eventManager,
                          final EventCallbacks callbacks)
    {
        super("account|acc", "littlebits.account", tr("handle littlebit accounts"));
        this.deviceDB = deviceDB;
        add(new AbstractCommand("add", "littlebits.account.add", "account", tr("add an account")) {
            @Override
            public boolean execute(final CommandSender commandSender, String alias, Map<String, Object> map, String... args) {
                if (args.length == 1) {
                    final Account account = deviceDB.addAccount(args[0]);
                    commandSender.sendMessage(tr("Added account with token {0}", account.getToken()));
                    callbacks.listenFor(AccountUpdated.class, new EventCallback<AccountUpdated>() {
                        @Override
                        public boolean call(AccountUpdated e) {
                            boolean sameAccount = e.getAccount().equals(account);
                            if (sameAccount) {
                                showAccountList(commandSender);
                            }
                            return sameAccount;
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        add(new AbstractCommand("remove", "littlebits.account.remove", "account", tr("remove an account")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                if (args.length == 1) {
                    if (deviceDB.removeAccount(args[0])) {
                        commandSender.sendMessage(tr("Removed account {0}", args[0]));
                    } else {
                        commandSender.sendMessage(tr("No account with id {0} was found", args[0]));
                    }
                    return true;
                }
                return false;
            }
        });
        add(new AbstractCommand("list", "littlebits.account.list", tr("list all current accounts")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... strings) {
                showAccountList(commandSender);
                return true;
            }
        });
        add(new AbstractCommand("update", "littlebits.account.update", "account", tr("updates the account info")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                List<Account> accounts = new ArrayList<>();
                if (args.length == 1) {
                    Account account = deviceDB.getAccount(args[0]);
                    if (account != null) {
                        accounts.add(account);
                    } else {
                        commandSender.sendMessage(tr("No account named {0} found", args[0]));
                    }
                } else if (args.length == 0) {
                    accounts.addAll(deviceDB.getAccounts());
                }
                if (!accounts.isEmpty()) {
                    for (Account account : accounts) {
                        eventManager.fireEvent(new AccountRemoved(account));
                        eventManager.fireEvent(new AccountAdded(account));
                        commandSender.sendMessage(tr("Updated account {0}", account.getDisplayName()));
                    }
                    return true;
                } else {
                    commandSender.sendMessage("No accounts found!");
                }
                return false;
            }
        });
        add(new AbstractCommand("label", "littlebits.account.label", "account label", tr("give an account a label")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                if (args.length == 2) {
                    Account account = deviceDB.getAccount(args[0]);
                    if (account != null) {
                        String oldName = account.getDisplayName();
                        account.setLabel(args[1]);
                        commandSender.sendMessage(tr("Changed label on {0} to {1}", oldName, args[1]));
                    } else {
                        commandSender.sendMessage(tr("No account named {0} found", args[0]));
                    }
                    return true;
                }
                return false;
            }
        });
        add(new AbstractCommand("token", "littlebits.account.token", "account", tr("Shows the account-token")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                if (args.length == 1) {
                    Account account = deviceDB.getAccount(args[0]);
                    if (account != null) {
                        commandSender.sendMessage(tr("Account {0} has token {1}", account.getDisplayName(), account.getToken()));
                    } else {
                        commandSender.sendMessage(tr("No account named {0} found", args[0]));
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void showAccountList(CommandSender commandSender) {
        String msg = tr("All accounts:") + "\n";
        for (Account account : deviceDB.getAccounts()) {
            msg += tr("  Account: {0}\n", account.getDisplayName());
            for (Device device : account.getDevices()) {
                msg += tr("    - {0}\n", device.toString());
            }
        }
        commandSender.sendMessage(msg.split("\n"));
    }

}
