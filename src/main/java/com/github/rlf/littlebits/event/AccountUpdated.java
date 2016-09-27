package com.github.rlf.littlebits.event;

import com.github.rlf.littlebits.model.Account;
import org.bukkit.event.HandlerList;

/**
 * An event fired when the device list on an account has been updated.
 */
public class AccountUpdated extends AbstractAccountEvent {
    private static final HandlerList handlers = new HandlerList();

    public AccountUpdated(Account account) {
        super(account);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
