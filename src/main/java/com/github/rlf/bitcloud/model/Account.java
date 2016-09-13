package com.github.rlf.bitcloud.model;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a CloudBit account.
 */
public class Account {
    private final String token;
    private List<Device> devices;

    public Account(String token) {
        this.token = token;
        devices = new CopyOnWriteArrayList<>();
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void add(Device device) {
        devices.add(device);
    }

    public void remove(Device device) {
        devices.remove(device);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return Objects.equals(token, account.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }
}
