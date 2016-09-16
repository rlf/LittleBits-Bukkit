package com.github.rlf.littlebits.cloudapi;

import com.github.rlf.littlebits.async.Scheduler;
import com.github.rlf.littlebits.event.AccountAdded;
import com.github.rlf.littlebits.event.AccountRemoved;
import com.github.rlf.littlebits.event.DeviceAdded;
import com.github.rlf.littlebits.event.DeviceOutput;
import com.github.rlf.littlebits.event.DeviceRemoved;
import com.github.rlf.littlebits.event.DeviceUpdated;
import com.github.rlf.littlebits.model.Account;
import com.github.rlf.littlebits.model.Device;
import com.github.rlf.littlebits.model.DeviceDB;
import dk.lockfuglsang.minecraft.yml.YmlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.IllegalPluginAccessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Bridge between the Bukkit event model and the CloudAPI
 */
public class BukkitCloudAPI implements Listener {
    private static final Logger LOG = Logger.getLogger(BukkitCloudAPI.class.getName());
    private static final int WAIT_DISCONNECTED = 30000; // Wait 30 seconds before re-checking
    private static final int WAIT_RATE_LIMIT = 500; // Try again in half a second
    private static final int WAIT_NORMAL = 250; // Most devices say 200 ms
    private static final int WAIT_OK = 5000; // Update output every 5 seconds

    private final Map<Integer, Integer> statusCodeWait = new HashMap<>();
    private final Scheduler scheduler;
    private final CloudAPI cloudAPI;
    private final DeviceDB deviceDB;
    private final Map<Device, DeviceReader> deviceReader = new ConcurrentHashMap<>();
    private final Map<Device, DeviceWriter> deviceWriter = new ConcurrentHashMap<>();

    public BukkitCloudAPI(Scheduler scheduler, CloudAPI cloudAPI, DeviceDB deviceDB, YmlConfiguration config) {
        this.scheduler = scheduler;
        this.cloudAPI = cloudAPI;
        this.deviceDB = deviceDB;
        statusCodeWait.put(200, WAIT_OK);
        statusCodeWait.put(404, WAIT_DISCONNECTED);
        statusCodeWait.put(429, WAIT_RATE_LIMIT);
        ConfigurationSection section = config.getConfigurationSection("cloudAPI.status-code-delay");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                if (key.matches("^[0-9]+$") && section.isInt(key)) {
                    statusCodeWait.put(Integer.parseInt(key, 10), section.getInt(key, WAIT_NORMAL));
                } else if (key.equals("default")) {
                    statusCodeWait.put(0, section.getInt(key, WAIT_NORMAL));
                }
            }
        }
    }

    @EventHandler
    public void on(AccountAdded e) {
        final Account account = e.getAccount();
        scheduler.async(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Device> devices = cloudAPI.getDevices(account);
                    deviceDB.updateAccount(account, devices);
                } catch (Exception e1) {
                    LOG.info("Error retrieving devices for account " + account + ": " + e1);
                }
            }
        });
    }

    @EventHandler
    public void on(AccountRemoved e) {
        final Account account = e.getAccount();
        synchronized (cloudAPI) {
            for (Device device : account.getDevices()) {
                removeDevice(device);
            }
        }
    }

    @EventHandler
    public void on(DeviceAdded e) {
        synchronized (cloudAPI) {
            Device device = e.getDevice();
            if (deviceReader.containsKey(device)) {
                removeDevice(device);
            }
            DeviceReader monitor = new DeviceReader(device);
            deviceReader.put(device, monitor);

            DeviceWriter writer = new DeviceWriter(device);
            deviceWriter.put(device, writer);
        }
    }

    @EventHandler
    public void on(DeviceRemoved e) {
        Device device = e.getDevice();
        removeDevice(device);
    }

    @EventHandler
    public void on(DeviceOutput e) {
        if (deviceWriter.containsKey(e.getDevice())) {
            deviceWriter.get(e.getDevice()).queue();
        }
    }

    @EventHandler
    public void on(final DeviceUpdated e) {
        scheduler.async(new Runnable() {
            @Override
            public void run() {
                int statusCode;
                do {
                   statusCode = cloudAPI.writeLabel(e.getDevice(), e.getDevice().getLabel());
                } while (statusCode != 200 && waitStatusCode(statusCode));
            }
        });
    }

    private void removeDevice(Device device) {
        DeviceReader monitor = deviceReader.remove(device);
        if (monitor != null) {
            monitor.cancel();
        }
    }

    public void shutdown() {
        synchronized (deviceReader) {
            for (DeviceReader monitor : deviceReader.values()) {
                monitor.cancel();
            }
            deviceReader.clear();
        }
        scheduler.shutdown();
        cloudAPI.shutdown();
    }

    private int getWaitTime(int status) {
        Integer wait = statusCodeWait.get(status);
        if (wait != null) {
            return wait.intValue();
        }
        wait = statusCodeWait.get(0);
        if (wait != null) {
            return wait.intValue();
        }
        return WAIT_NORMAL;
    }

    private class DeviceReader implements Runnable, com.github.rlf.littlebits.async.Consumer<CloudAPI.DeviceInput> {

        private final Device device;
        private final Scheduler.Task task;
        private volatile boolean cancelled = false;
        public DeviceReader(Device device) {
            this.device = device;
            task = scheduler.async(this);
        }

        private boolean isCancelled() {
            return Thread.currentThread().isInterrupted() || cancelled;
        }

        @Override
        public void run() {
            while (!isCancelled()) {
                int status = 500;
                try {
                    status = cloudAPI.readInput(device, this);
                    if (isCancelled()) {
                        return;
                    }
                } catch (Exception e) {
                    deviceDB.removeDevice(device);
                }
                waitStatusCode(status);
            }
        }

        public void cancel() {
            task.cancel();
            cancelled = true;
        }

        @Override
        public void accept(CloudAPI.DeviceInput deviceInput) {
            deviceDB.setInput(device, deviceInput.getPercentage());
        }

    }

    private boolean waitStatusCode(int status) {
        try {
            Thread.sleep(getWaitTime(status));
            return true;
        } catch (InterruptedException e) {
            // Ignored
            return false;
        }
    }

    /**
     * Responsible for throttling output reg. a device to the CloudAPI.
     */
    private class DeviceWriter implements Runnable {
        private final Device device;
        private Scheduler.Task pendingTask;

        public DeviceWriter(Device device) {
            this.device = device;
        }

        @Override
        public void run() {
            int status = cloudAPI.writeOutput(device, device.getOut()*100/15);
            queue(getWaitTime(status));
        }

        public void queue() {
            queue(200);
        }

        public void queue(int delay) {
            cancel();
            try {
                pendingTask = scheduler.async(this, delay);
            } catch (IllegalPluginAccessException e) {
                // Suppress exceptions when the plugin has been disabled
            }
        }

        public void cancel() {
            if (pendingTask != null) {
                pendingTask.cancel();
            }
        }
    }
}
