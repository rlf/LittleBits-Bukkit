package com.github.rlf.bitcloud.model;

/**
 * Represents a littlebits device.
 */
public class Device {
    /**
     * The ID from the CloudAPI.
     */
    private final String id;
    /**
     * The currently chosen label for the device.
     */
    private String label;

    /**
     * The last read amplitude from the device.
     */
    private double amplitude;

    /**
     * Whether the device is currently connected.
     */
    private boolean connected;

    public Device(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
