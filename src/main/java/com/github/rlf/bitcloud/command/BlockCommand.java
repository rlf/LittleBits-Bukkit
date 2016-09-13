package com.github.rlf.bitcloud.command;

import com.github.rlf.bitcloud.model.BlockDB;
import com.github.rlf.bitcloud.model.LittlebitsBlock;
import dk.lockfuglsang.minecraft.command.AbstractCommand;
import dk.lockfuglsang.minecraft.command.CompositeCommand;
import org.bukkit.command.CommandSender;

import java.util.Map;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

public class BlockCommand extends CompositeCommand {
    public BlockCommand(final BlockDB blockDB) {
        super("block|bl", "bitcloud.block", tr("manages blocks"));
        add(new AbstractCommand("list|ls", "bitcloud.block.list", tr("lists all littlebit blocks")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                String msg = tr("Blocks:\n");
                for (LittlebitsBlock block : blockDB.getBlocks()) {
                    msg += tr(" - {0}\n", block);
                }
                commandSender.sendMessage(msg.split("\n"));
                return true;
            }
        });
    }
}
