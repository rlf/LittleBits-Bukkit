package com.github.rlf.bitcloud.model;

import java.util.List;

/**
 */
public interface DeviceDB extends AbstractDB {
    List<Account> getAccounts();

    Account getAccount(String id);

    Account addAccount(String token);

    Device getDevice(String id);

    /**
     * Supports swapping between devices
     * @param device Old device (may be <code>null</code>)
     * @return The next device (may be <code>null</code>)
     */
    Device getNextDevice(Device device);
}
