package com.github.rlf.bitcloud.event;

import com.github.rlf.bitcloud.model.Account;
import com.github.rlf.bitcloud.model.Device;
import org.bukkit.event.Event;

/**
 * An event fired when one of the registered devices has been connected.
 */
public abstract class AbstractAccountEvent extends Event {
    private Account account;

    public AbstractAccountEvent(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
}
