package com.github.rlf.littlebits.event;

import com.github.rlf.littlebits.model.Account;
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
