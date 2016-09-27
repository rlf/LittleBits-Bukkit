package com.github.rlf.littlebits.command;

import com.github.rlf.littlebits.event.DeviceAdded;
import com.github.rlf.littlebits.event.DeviceRemoved;
import com.github.rlf.littlebits.event.EventManager;
import com.github.rlf.littlebits.model.Device;
import com.github.rlf.littlebits.model.DeviceDB;
import com.github.rlf.littlebits.model.LogEntry;
import dk.lockfuglsang.minecraft.command.AbstractCommand;
import dk.lockfuglsang.minecraft.command.CompositeCommand;
import dk.lockfuglsang.minecraft.command.completion.AbstractTabCompleter;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

public class DeviceCommand extends CompositeCommand {

    public DeviceCommand(final DeviceDB deviceDB, final EventManager eventManager) {
        super("device|dev", "littlebits.device", tr("manage devices"));
        add(new AbstractCommand("info|i", "littlebits.device.info", "device", tr("show device info")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                if (args.length == 1) {
                    Device device = deviceDB.getDevice(args[0]);
                    if (device != null) {
                        commandSender.sendMessage(tr("Device {0}", device));
                    } else {
                        commandSender.sendMessage(tr("No device {0} was found", args[0]));
                    }
                    return true;
                }
                return false;
            }
        });
        add(new AbstractCommand("remove", "littlebits.device.remove", "device", tr("remove device")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                if (args.length == 1) {
                    Device device = deviceDB.getDevice(args[0]);
                    if (device != null && deviceDB.removeDevice(device)) {
                        commandSender.sendMessage(tr("Device {0} removed", device.getLabel()));
                    } else {
                        commandSender.sendMessage(tr("No device {0} was found", args[0]));
                    }
                    return true;
                }
                return false;
            }
        });
        add(new AbstractCommand("update", "littlebits.device.update", "device", tr("update device")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                if (args.length == 1) {
                    Device device = deviceDB.getDevice(args[0]);
                    if (device != null) {
                        eventManager.fireEvent(new DeviceRemoved(device));
                        eventManager.fireEvent(new DeviceAdded(device));
                        commandSender.sendMessage(tr("Device {0} updated", device.getLabel()));
                    } else {
                        commandSender.sendMessage(tr("No device {0} was found", args[0]));
                    }
                    return true;
                }
                return false;
            }
        });
        add(new AbstractCommand("output", "littlebits.device.output", "device 0-15", tr("set output of device")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                if (args.length == 2 && args[1].matches("^[0-9]+$")) {
                    Device device = deviceDB.getDevice(args[0]);
                    int amplitude = Integer.parseInt(args[1], 10);
                    if (device != null) {
                        if (amplitude >= 0 && amplitude <= 15) {
                            deviceDB.setOutput(device, amplitude);
                            commandSender.sendMessage(tr("Device {0} output set to {1}", device.getLabel(), amplitude));
                        } else {
                            commandSender.sendMessage(tr("Device output must be between 0-15"));
                        }
                    } else {
                        commandSender.sendMessage(tr("No device {0} was found", args[0]));
                    }
                    return true;
                }
                return false;
            }
        });
        add(new AbstractCommand("input", "littlebits.device.input", "device pct", tr("simulate input from device")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                if (args.length == 2 && args[1].matches("^[0-9]+$")) {
                    Device device = deviceDB.getDevice(args[0]);
                    int amplitude = Integer.parseInt(args[1], 10);
                    if (device != null) {
                        if (amplitude >= 0 && amplitude <= 100) {
                            deviceDB.setInput(device, amplitude);
                            commandSender.sendMessage(tr("Device {0} input set to {1}", device.getLabel(), amplitude));
                        } else {
                            commandSender.sendMessage(tr("Device input must be between 0-100"));
                        }
                    } else {
                        commandSender.sendMessage(tr("No device {0} was found", args[0]));
                    }
                    return true;
                }
                return false;
            }
        });
        /*
        add(new AbstractCommand("label", "littlebits.device.label", "device label", tr("change device-label")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                if (args.length == 2) {
                    Device device = deviceDB.getDevice(args[0]);
                    String label = args[1];
                    if (device != null) {
                        String oldLabel = device.getLabel();
                        deviceDB.setLabel(device, label);
                        commandSender.sendMessage(tr("Device {0} changed label to {1}", oldLabel, label));
                    } else {
                        commandSender.sendMessage(tr("No device {0} was found", args[0]));
                    }
                    return true;
                }
                return false;
            }
        });
        */
        add(new AbstractCommand("log", "littlebits.device.log", "device ?search", tr("display device-log")) {
            @Override
            public boolean execute(CommandSender commandSender, String s, Map<String, Object> map, String... args) {
                if (args.length >= 1) {
                    String search = args.length > 1 ? join(args).substring(args[0].length()+1) : null;
                    Device device = deviceDB.getDevice(args[0]);
                    if (device == null) {
                        commandSender.sendMessage(tr("No device {0} was found", args[0]));
                    } else {
                        String msg = tr("Device log for {0}", device) + "\n";
                        List<LogEntry> log = deviceDB.getLog(device, search, 0, 10);
                        if (log.isEmpty()) {
                            msg += tr("\u00a77 - no log-entries") + "\n";
                        } else {
                            for (LogEntry entry : log) {
                                msg += entry.toString() + "\n";
                            }
                        }
                        commandSender.sendMessage(msg.trim().split("\n"));
                    }
                    return true;
                }
                return false;
            }
        });
    }

}
