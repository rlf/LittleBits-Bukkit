package com.github.rlf.bitcloud.model;

import dk.lockfuglsang.minecraft.file.FileUtil;
import dk.lockfuglsang.minecraft.yml.YmlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by R4zorax on 12/09/2016.
 */
public class FileDeviceDB implements DeviceDB {
    private final List<Account> accounts = new CopyOnWriteArrayList<>();

    private YmlConfiguration config;

    public FileDeviceDB() {
        load();
    }

    @Override
    public List<Account> getAccounts() {
        return null;
    }

    @Override
    public Account getAccount(String id) {
        return null;
    }

    @Override
    public Account addAccount(String token) {
        return null;
    }

    @Override
    public Device getDevice(String id) {
        return null;
    }

    @Override
    public Device getNextDevice(Device device) {
        return null;
    }

    @Override
    public void load() {
        accounts.clear();
        config = FileUtil.getYmlConfiguration("config.yml");
        ConfigurationSection sec = config.getConfigurationSection("accounts");
        if (sec != null) {
            for (String token : sec.getKeys(false)) {
                Account account = new Account(token);
            }
        }
    }

    @Override
    public void save() {

    }
}
