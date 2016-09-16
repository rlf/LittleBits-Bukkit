package com.github.rlf.littlebits.command;

import com.github.rlf.littlebits.model.BlockDB;
import com.github.rlf.littlebits.model.BlockLocation;
import com.github.rlf.littlebits.model.Device;
import com.github.rlf.littlebits.model.DeviceDB;
import com.github.rlf.littlebits.model.LittlebitsBlock;
import dk.lockfuglsang.minecraft.command.AbstractCommand;
import dk.lockfuglsang.minecraft.command.CompositeCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

public class BlockCommand extends CompositeCommand {
    public BlockCommand(final BlockDB blockDB, final DeviceDB deviceDB) {
        super("block|bl", "littlebits.block", tr("manages blocks"));
        add(new AbstractCommand("list", "littlebits.block.list", tr("lists all littlebit blocks")) {
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
        add(new AbstractCommand("assign", "littlebits.block.assign", "block device", tr("assigns a device to a block")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                if (args.length == 2) {
                    BlockLocation loc = BlockLocation.wrap(args[0]);
                    Device device = deviceDB.getDevice(args[1]);
                    if (loc == null) {
                        commandSender.sendMessage(tr("{0} is not a valid block-location", args[0]));
                    }
                    boolean invalidDevice = device == null && !args[1].equals(tr("-none-"));
                    if (invalidDevice) {
                        commandSender.sendMessage(tr("No device {0} was found", args[1]));
                    }
                    if (loc != null && !invalidDevice) {
                        LittlebitsBlock block = blockDB.getBlock(loc.toLocation());
                        if (block != null) {
                            blockDB.assignDevice(block, device);
                            if (device != null) {
                                commandSender.sendMessage(tr("Changed to device {0}", device.getLabel()));
                            } else {
                                commandSender.sendMessage(tr("Disabled this littlebits block."));
                            }
                        } else {
                            commandSender.sendMessage(tr("No littleBits block found at that location"));
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        add(new AbstractCommand("give", "littlebits.block.give", "?player", tr("gives the player a littlebits block")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                Player player = null;
                if (args.length == 1) {
                    player = Bukkit.getPlayer(args[0]);
                } else if (commandSender instanceof Player) {
                    player = (Player)commandSender;
                }
                if (player != null) {
                    player.getInventory().addItem(LittlebitsBlock.ITEM_STACK.clone());
                    commandSender.sendMessage(tr("Gave {0} a littleBits block", player.getName()));
                    if (player != commandSender) {
                        player.sendMessage(tr("You where given a littleBits block by {0}", commandSender.getName()));
                    }
                } else {
                    commandSender.sendMessage(tr("No valid player found"));
                    return false;
                }
                return true;
            }
        });

        addFeaturePermission("littlebits.block.place", tr("allow placement of littleBits"));
        addFeaturePermission("littlebits.block.break", tr("allow breaking of littleBits"));
        addFeaturePermission("littlebits.block.info", tr("allow left-clicking littleBits"));
    }
}
