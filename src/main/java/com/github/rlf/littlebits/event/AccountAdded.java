package com.github.rlf.littlebits.event;

import com.github.rlf.littlebits.model.Account;
import org.bukkit.event.HandlerList;

/**
 * An event fired when one of the registered devices receives input (from redstone).
 */
public class AccountAdded extends AbstractAccountEvent {
    private static final HandlerList handlers = new HandlerList();

    public AccountAdded(Account account) {
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
