package com.github.rlf.littlebits.model;

import com.github.rlf.littlebits.event.AccountAdded;
import com.github.rlf.littlebits.event.AccountRemoved;
import com.github.rlf.littlebits.event.AccountUpdated;
import com.github.rlf.littlebits.event.DeviceAdded;
import com.github.rlf.littlebits.event.DeviceConnected;
import com.github.rlf.littlebits.event.DeviceDisconnected;
import com.github.rlf.littlebits.event.DeviceInput;
import com.github.rlf.littlebits.event.DeviceOutput;
import com.github.rlf.littlebits.event.DeviceRemoved;
import com.github.rlf.littlebits.event.DeviceUpdated;
import com.github.rlf.littlebits.event.EventManager;
import dk.lockfuglsang.minecraft.file.FileUtil;
import dk.lockfuglsang.minecraft.yml.YmlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import static dk.lockfuglsang.minecraft.po.I18nUtil.marktr;
import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

/**
 * File based device database
 */
public class FileDeviceDB implements DeviceDB {
    private static final Logger log = Logger.getLogger(FileDeviceDB.class.getName());
    private static final int MAX_LOG = 500;

    private final List<Account> accounts = new CopyOnWriteArrayList<>();
    private final Map<Device, List<LogEntry>> deviceLog = new ConcurrentHashMap<>();

    private YmlConfiguration config;
    private EventManager eventManager;

    public FileDeviceDB(EventManager eventManager) {
        this.eventManager = eventManager;
        config = FileUtil.getYmlConfiguration("devices.yml");
    }

    @Override
    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    @Override
    public Account getAccount(String id) {
        if (id == null) {
            return null;
        }
        for (Account account : accounts) {
            if (account.getToken().equals(id) || account.getDisplayName().equals(id)) {
                return account;
            }
        }
        return null;
    }

    @Override
    public Account addAccount(String token) {
        Account account = new Account(token);
        return addAccount(account);
    }

    private Account addAccount(Account account) {
        accounts.add(account);
        eventManager.fireEvent(new AccountAdded(account));
        return account;
    }

    @Override
    public Account updateAccount(Account account, List<Device> newDevices) {
        List<Device> oldDevices = new ArrayList<>(account.getDevices());
        List<Device> updatedDevices = new ArrayList<>(oldDevices);
        List<Device> removedDevices = new ArrayList<>(oldDevices);
        updatedDevices.retainAll(newDevices); // Those that should be updated
        removedDevices.removeAll(newDevices); // Those that should be deleted
        for (Device device : removedDevices) {
            account.getDevices().remove(device);
            addLog(device, marktr("DEV Removed"));
            eventManager.fireEvent(new DeviceRemoved(device));
        }
        for (Device device : updatedDevices) {
            Device newDevice = newDevices.get(newDevices.indexOf(device));
            setLabel(device, newDevice.getLabel());
        }
        newDevices.removeAll(updatedDevices);
        for (Device device : newDevices) {
            account.getDevices().add(device);
            addLog(device, marktr("DEV Added"));
            eventManager.fireEvent(new DeviceAdded(device));
        }
        eventManager.fireEvent(new AccountUpdated(account));
        return account;
    }

    @Override
    public boolean removeAccount(String token) {
        Account account = getAccount(token);
        return removeAccount(account);
    }

    private boolean removeAccount(Account account) {
        if (account != null) {
            try {
                return accounts.remove(account);
            } finally {
                eventManager.fireEvent(new AccountRemoved(account));
            }
        }
        return false;
    }

    @Override
    public void setConnected(Device device, boolean connected) {
        if (device == null) {
            return;
        }
        boolean oldConnected = device.isConnected();
        device.setConnected(connected);
        if (oldConnected != connected) {
            if (connected) {
                eventManager.fireEvent(new DeviceConnected(device));
            } else {
                eventManager.fireEvent(new DeviceDisconnected(device));
            }
        }
    }

    @Override
    public void setInput(Device device, int percentage) {
        if (device == null) {
            return;
        }
        setConnected(device, true);
        int amplitude = (percentage * 15) / 100;
        int oldIn = device.getIn();
        if (oldIn != amplitude) {
            device.setIn(amplitude);
            addLog(device, tr("INP << {0} ({1}%)", amplitude, percentage));
            eventManager.fireEvent(new DeviceInput(device, oldIn));
        }
    }

    @Override
    public void setOutput(Device device, int amplitude) {
        if (device == null) {
            return;
        }
        int oldOut = device.getOut();
        if (oldOut != amplitude) {
            device.setOut(amplitude);
            addLog(device, tr("OUT >> {0}", amplitude));
            eventManager.fireEvent(new DeviceOutput(device, oldOut));
        }
    }

    @Override
    public void setLabel(Device device, String label) {
        if (device != null && !label.equals(device.getLabel())) {
            device.setLabel(label);
            addLog(device, marktr("DEV Updated"));
            eventManager.fireEvent(new DeviceUpdated(device));
        }
    }

    @Override
    public List<Device> getDevices() {
        List<Device> devices = new ArrayList<>();
        for (Account account : accounts) {
            devices.addAll(account.getDevices());
        }
        Collections.sort(devices, new DeviceComparator());
        return devices;
    }

    @Override
    public Device getDevice(String id) {
        if (id == null) {
            return null;
        }
        for (Account account : accounts) {
            for (Device device : account.getDevices()) {
                if (id.equals(device.getId()) || id.equals(device.getLabel())) {
                    return device;
                }
            }
        }
        return null;
    }

    @Override
    public Device getNextDevice(Device currentDevice) {
        Device firstDevice = null;
        boolean returnNext = currentDevice == null;
        for (Account account : accounts) {
            for (Device device : account.getDevices()) {
                if (firstDevice == null) {
                    firstDevice = device;
                }
                if (returnNext) {
                    return device;
                }
                if (currentDevice.equals(device)) {
                    returnNext = true;
                }
            }
        }
        return null;
    }

    @Override
    public boolean removeDevice(Device device) {
        if (device == null) {
            return false;
        }
        try {
            return device.getAccount().getDevices().remove(device);
        } finally {
            eventManager.fireEvent(new DeviceRemoved(device));
        }
    }

    @Override
    public void addLog(Device device, String message) {
        synchronized (deviceLog) {
            if (!deviceLog.containsKey(device)) {
                deviceLog.put(device, new ArrayList<LogEntry>());
            }
            List<LogEntry> log = deviceLog.get(device);
            log.add(0, new LogEntry(message));
            Collections.sort(deviceLog.get(device));
            while (log.size() > MAX_LOG) {
                log.remove(log.size()-1);
            }
        }
    }

    @Override
    public List<LogEntry> getLog(Device device, String search, int offset, int length) {
        List<LogEntry> log = deviceLog.get(device);
        if (log == null || log.isEmpty() || log.size() < offset) {
            return Collections.emptyList();
        }
        ArrayList<LogEntry> copy = new ArrayList<>(log);
        if (search != null && !search.isEmpty()) {
            for (Iterator<LogEntry> it = copy.iterator(); it.hasNext();) {
                if (!it.next().toString().contains(search)) {
                    it.remove();
                }
            }
        }
        if (!copy.isEmpty() && offset >= 0 && offset < copy.size()) {
            return new ArrayList<>(copy.subList(offset, Math.min(log.size(), offset + length)));
        }
        return Collections.emptyList();
    }

    @Override
    public void load() {
        for (Account account : accounts) {
            removeAccount(account);
        }
        accounts.clear();
        config = FileUtil.loadConfig(FileUtil.getConfigFile("devices.yml"));
        ConfigurationSection sec = config.getConfigurationSection("accounts");
        if (sec != null) {
            for (String token : sec.getKeys(false)) {
                String label = sec.getString(token + ".label", null);
                Account account = new Account(token, label);
                ConfigurationSection deviceSection = sec.getConfigurationSection(token + ".devices");
                addAccount(account);
                if (deviceSection != null) {
                    List<Device> devices = new ArrayList<>();
                    for (String deviceId : deviceSection.getKeys(false)) {
                        devices.add(new Device(account, deviceId,
                                deviceSection.getString(deviceId + ".label", "noname"), false));
                    }
                    updateAccount(account, devices);
                }
            }
        }
    }

    @Override
    public void save() {
        try {
            config.set("accounts", null);
            for (Account account : accounts) {
                ConfigurationSection section = config.createSection("accounts." + account.getToken());
                section.set("label", account.getDisplayName());
                ConfigurationSection devices = section.createSection("devices");
                for (Device device : account.getDevices()) {
                    devices.set(device.getId() + ".label", device.getLabel());
                    devices.set(device.getId() + ".in", device.getIn());
                    devices.set(device.getId() + ".out", device.getOut());
                }
            }
            config.save(FileUtil.getConfigFile("devices.yml"));
        } catch (IOException e) {
            log.info("Unable to save file: " + e);
        }
    }
}
