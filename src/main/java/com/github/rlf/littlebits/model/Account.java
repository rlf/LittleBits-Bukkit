package com.github.rlf.littlebits.model;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a CloudBit account.
 */
public class Account {
    private final String token;
    private String label;

    private List<Device> devices;

    public Account(String token) {
        this(token, null);
    }
    public Account(String token, String label) {
        this.token = token;
        this.label = label;
        devices = new CopyOnWriteArrayList<>();
    }

    public String getToken() {
        return token;
    }

    public String getDisplayName() {
        return label != null
                ? label
                : token != null && token.length() > 8
                ? token.substring(0,4) + "-" + token.substring(token.length()-4)
                : token;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Device> getDevices() {
        return devices;
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

    @Override
    public String toString() {
        return "Account{" +
                "token='" + token + '\'' +
                ", devices=" + devices +
                '}';
    }
}
