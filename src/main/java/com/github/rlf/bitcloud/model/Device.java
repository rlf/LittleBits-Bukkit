package com.github.rlf.bitcloud.model;

/**
 * Represents a littlebits device.
 */
public class Device {
    /**
     * The ID from the CloudAPI.
     */
    private final String id;

    private final Account account;

    /**
     * The currently chosen label for the device.
     */
    private String label;

    /**
     * Current input of the littlebits device.
     */
    private double in;

    /**
     * Current output of the littlebits device
     */
    private double out;

    /**
     * Whether the device is currently connected.
     */
    private boolean connected;

    public Device(Account account, String id, String label) {
        this.id = id;
        this.account = account;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public double getIn() {
        return in;
    }

    public void setOut(double out) {
        this.out = out;
    }

    public double getOut() {
        return out;
    }

    public Account getAccount() {
        return account;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
