package com.github.rlf.littlebits.model;

import java.util.List;

/**
 * The primary database of devices.
 * Responsible for keeping track of the devices and firing events.
 */
public interface DeviceDB extends AbstractDB {
    /**
     * Returns all currently registered accounts.
     * @return all currently registered accounts.
     */
    List<Account> getAccounts();

    /**
     * Returns the matching account.
     * @param id   either label, short-name or token
     * @return The account or <code>null</code>.
     */
    Account getAccount(String id);

    /**
     * Adds an account to the database, and fires the appropriate events.
     * @param token The complete token of the new account (64 characters).
     * @return Returns the newly added account object.
     */
    Account addAccount(String token);

    /**
     * Updates the account to reflect the new devices, and fires appropriate device events.
     * @param account The account to update.
     * @param devices The new list of devices associated with that account.
     * @return The updated account.
     */
    Account updateAccount(Account account, List<Device> devices);

    /**
     * Removes the account and fires the appropriate events.
     * @param id either label, short-name or token
     * @return <code>true</code> if the account was successfully removed.
     */
    boolean removeAccount(String id);

    /**
     * Changes connection-state of a device, and fires events.
     * @param device    The device
     * @param connected The connection-state.
     */
    void setConnected(Device device, boolean connected);

    /**
     * Sets the input (0-100), i.e. the minecraft block output (0-15).
     * Note: This will fire appropriate events - if needed.
     * @param device     The device
     * @param percentage The input in pct (0-100).
     */
    void setInput(Device device, int percentage);

    /**
     * Sets the output (0-15), i.e. the real-world block output (0-100).
     * Note: This will fire appropriate events - if needed.
     * @param device    The device
     * @param amplitude The redstone amplitude (0-15).
     */
    void setOutput(Device device, int amplitude);

    /**
     * Updates the device-label and fires events.
     * @param device The device.
     * @param label  The new label of the device.
     */
    void setLabel(Device device, String label);

    /**
     * Returns a complete list of currently registered devices.
     * @return a complete list of currently registered devices.
     */
    List<Device> getDevices();

    /**
     * Returns the first matching device.
     * @param id Either label or id.
     * @return the first matching device or <code>null</code>.
     */
    Device getDevice(String id);

    /**
     * Supports swapping between devices
     * @param device Old device (may be <code>null</code>)
     * @return The next device (may be <code>null</code>)
     */
    Device getNextDevice(Device device);

    /**
     * Removes the supplied device from the database and fires events.
     * @param device The device to remove
     * @return <code>true</code> if the device existed and was removed.
     */
    boolean removeDevice(Device device);

    /**
     * Adds a device-log entry.
     * @param device  The device to log a message for
     * @param message The message to log.
     */
    void addLog(Device device, String message);

    /**
     * Returns a list of log-entries for what happened to the device.
     * @param device The device to see a log for
     * @param search A search term to filter for in the log
     *@param offset An offset in the log.
     * @param length The (max) number of log-entries to return.   @return A (possibly empty) list of log-entries.
     */
    List<LogEntry> getLog(Device device, String search, int offset, int length);
}
