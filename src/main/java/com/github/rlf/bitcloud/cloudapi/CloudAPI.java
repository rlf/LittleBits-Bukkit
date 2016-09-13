package com.github.rlf.bitcloud.cloudapi;

import com.github.rlf.bitcloud.model.Account;
import com.github.rlf.bitcloud.model.Device;

import java.util.List;

/**
 * An abstraction of the CloudAPI
 */
public interface CloudAPI {
    /**
     * Returns the list of devices from the given account.
     * NOTE: Should NEVER be called on the primary server thread.
     * @param account An account
     * @return
     */
    List<Device> getDevices(Account account) throws APIException;
}
