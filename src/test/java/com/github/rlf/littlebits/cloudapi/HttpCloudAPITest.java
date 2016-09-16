package com.github.rlf.littlebits.cloudapi;

import com.github.rlf.littlebits.model.Account;
import com.github.rlf.littlebits.model.Device;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * CUDiM Coding Pirate specific test.
 */
public class HttpCloudAPITest {
    private static final String ACCOUNT_TOKEN = "d64e5f77f9c60f00b4999ca2539ef61394e7b3beef00ce0856cb1aa9976871fa";

    @Test
    public void getDevicesClean() throws Exception {
        HttpCloudAPI cloudAPI = new HttpCloudAPI();

        Account account = new Account(ACCOUNT_TOKEN);
        List<Device> devices = cloudAPI.getDevices(account);
        assertThat(devices, notNullValue());
        assertThat(devices.size(), is(1));

        assertThat(devices.get(0), is(new Device(account, "243c200c0659", "Snivy")));
    }

    @Test
    public void getDevicesUpdate() throws Exception {
        HttpCloudAPI cloudAPI = new HttpCloudAPI();

        Account account = new Account(ACCOUNT_TOKEN);
        Device device1 = new Device(account, "00e04c02b9a2", "kodepirat-old1", true);
        account.getDevices().add(device1);
        account.getDevices().add(new Device(account, "00e04c02b933", "kodepirat-old4", false));

        List<Device> devices = cloudAPI.getDevices(account);
        assertThat(devices.size(), is(1));

        assertThat(devices.get(0), is(new Device(account, "243c200c0659", "Snivy")));
        assertThat(devices.get(0), not(sameInstance(device1)));
    }
}