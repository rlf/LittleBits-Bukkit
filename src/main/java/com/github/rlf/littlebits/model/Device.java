package com.github.rlf.littlebits.model;

import java.util.Objects;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

/**
 * Represents a littlebits device.
 */
public class Device {
    private final Account account;

    /**
     * The ID from the CloudAPI.
     */
    private final String id;

    /**
     * The currently chosen label for the device.
     */
    private String label;

    /**
     * Current input of the littlebits device (in percentage; 0-100).
     */
    private int in;

    /**
     * Current output of the littlebits device (in percentage; 0-100).
     */
    private int out;

    /**
     * Whether the device is currently connected.
     */
    private boolean connected;

    public Device(Account account, String id, String label) {
        this(account, id, label, false);
    }

    public Device(Account account, String id, String label, boolean connected) {
        this.id = id;
        this.account = account;
        this.label = label;
        this.connected = connected;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setIn(int in) {
        this.in = in;
    }

    /**
     * The input received from the littlebits device in the real world.
     * @return an integer from 0-15.
     */
    public int getIn() {
        return in;
    }

    /**
     * The output the realworld device should output.
     * @return an integer from 0-15.
     */
    public void setOut(int out) {
        this.out = out;
    }

    public int getOut() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Device)) return false;
        Device device = (Device) o;
        return Objects.equals(id, device.id) &&
                Objects.equals(account, device.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account);
    }

    @Override
    public String toString() {
        return tr("{0} ({1}:{2}, in={3}, out={4}, {5})",
                label,
                (account != null ? account.getDisplayName() : tr("-none-")),
                id,
                in,
                out,
                (connected ? tr("connected") : tr("disconnected")));
    }
}
