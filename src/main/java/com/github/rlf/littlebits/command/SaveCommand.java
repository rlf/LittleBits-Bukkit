package com.github.rlf.littlebits.command;

import com.github.rlf.littlebits.model.AbstractDB;
import dk.lockfuglsang.minecraft.command.AbstractCommand;
import org.bukkit.command.CommandSender;

import java.util.Map;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

/**
 * Saves all databases.
 */
public class SaveCommand extends AbstractCommand {
    private final AbstractDB[] dbs;

    public SaveCommand(AbstractDB... dbs) {
        super("save|s", "littlebits.save", tr("saves the configs to file"));
        this.dbs = dbs;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... strings) {
        for (AbstractDB db : dbs) {
            db.save();
        }
        commandSender.sendMessage(tr("Configuration saved"));
        return true;
    }
}
