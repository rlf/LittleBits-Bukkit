package com.github.rlf.bitcloud.command;

import dk.lockfuglsang.minecraft.command.CompositeCommand;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

/**
 * Responsible for editing account information.
 */
public class AccountCommand extends CompositeCommand {

    public AccountCommand() {
        super("account|acc", "bitcloud.account", tr("handle littlebit accounts"));
    }

}
