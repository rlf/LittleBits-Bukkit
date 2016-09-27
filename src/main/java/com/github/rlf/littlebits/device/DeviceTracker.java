package com.github.rlf.littlebits.device;

import com.github.rlf.littlebits.event.DeviceLogEvent;
import com.github.rlf.littlebits.event.EventManager;
import com.github.rlf.littlebits.model.Device;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

/**
 * Responsible for tracking a device (for debugging purposes).
 */
public class DeviceTracker implements Listener {
    private final Map<Device, Set<CommandSender>> trackers = new ConcurrentHashMap<>();

    public DeviceTracker(EventManager eventManager) {
        eventManager.registerListener(this);
    }

    public void track(Device device, CommandSender sender) {
        synchronized (trackers) {
            if (!trackers.containsKey(device)) {
                trackers.put(device, new HashSet<CommandSender>());
            }
            trackers.get(device).add(sender);
        }
    }

    public boolean untrack(Device device, CommandSender sender) {
        synchronized (trackers) {
            if (trackers.containsKey(device)) {
                Set<CommandSender> set = trackers.get(device);
                try {
                    return set.remove(sender);
                } finally {
                    if (set.isEmpty()) {
                        trackers.remove(device);
                    }
                }
            }
        }
        return false;
    }

    @EventHandler
    public void on(DeviceLogEvent event) {
        synchronized (trackers) {
            Device device = event.getDevice();
            if (trackers.containsKey(device)) {
                for (Iterator<CommandSender> it = trackers.get(device).iterator(); it.hasNext();) {
                    CommandSender commandSender = it.next();
                    if (commandSender == null || ((commandSender instanceof Player && !((Player) commandSender).isOnline()))) {
                        it.remove();
                    } else {
                        commandSender.sendMessage(tr("\u00a77{0} - \u00a79{1}", device.getLabel(), event.getEntry().getMessage()));
                    }
                }
            }
        }
    }
}
