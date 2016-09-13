package com.github.rlf.bitcloud.cloudapi;

import com.github.rlf.bitcloud.model.Account;
import com.github.rlf.bitcloud.model.Device;
import org.bukkit.Bukkit;

import java.util.List;

/**
 * A simple apache http client implementation of the CloudAPI.
 */
public class HttpCloudAPI implements CloadAPI {
    @Override
    public List<Device> getDevices(Account account) throws APIException {
        if (Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Trying to access CloudAPI on primary thread");
        }
        return null;
    }
}
