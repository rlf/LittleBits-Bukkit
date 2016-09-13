package com.github.rlf.bitcloud.model;

import com.github.rlf.bitcloud.event.AccountAdded;
import com.github.rlf.bitcloud.event.AccountRemoved;
import com.github.rlf.bitcloud.event.EventManager;
import dk.lockfuglsang.minecraft.file.FileUtil;
import dk.lockfuglsang.minecraft.yml.YmlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * File based device database
 */
public class FileDeviceDB implements DeviceDB {
    private static final Logger log = Logger.getLogger(FileDeviceDB.class.getName());

    private final List<Account> accounts = new CopyOnWriteArrayList<>();

    private YmlConfiguration config;
    private EventManager eventManager;

    public FileDeviceDB(EventManager eventManager) {
        this.eventManager = eventManager;
        load();
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
            if (account.getToken().equals(id)) {
                return account;
            }
        }
        return null;
    }

    @Override
    public Account addAccount(String token) {
        Account account = new Account(token);
        accounts.add(account);
        eventManager.fireEvent(new AccountAdded(account));
        return account;
    }

    @Override
    public boolean removeAccount(String token) {
        Account account = getAccount(token);
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
        return firstDevice;
    }

    @Override
    public void load() {
        accounts.clear();
        config = FileUtil.getYmlConfiguration("config.yml");
        ConfigurationSection sec = config.getConfigurationSection("accounts");
        if (sec != null) {
            for (String token : sec.getKeys(false)) {
                addAccount(token);
                // TODO: Rasmus - 13-09-2016: Save and handle devices as well?
            }
        }
    }

    @Override
    public void save() {
        try {
            config.set("accounts", null);
            for (Account account : accounts) {
                ConfigurationSection section = config.createSection("accounts." + account.getToken());
                ConfigurationSection devices = section.createSection("devices");
                for (Device device : account.getDevices()) {
                    devices.set(device.getId() + ".label", device.getLabel());
                    devices.set(device.getId() + ".in", device.getIn());
                    devices.set(device.getId() + ".out", device.getOut());
                }
            }
            config.save(FileUtil.getConfigFile("config.yml"));
        } catch (IOException e) {
            log.info("Unable to save file: " + e);
        }
    }
}
