package com.my.commandservice.entity.enumeration;

public enum AccountStatus {

    ACTIVE(true, true),
    BLOCKED(false, false),
    FROZEN(false, false),
    CLOSED(false, false),
    PENDING(false, false);

    private final boolean canWithdraw;
    private final boolean canDeposit;

    AccountStatus(boolean canWithdraw, boolean canDeposit) {
        this.canWithdraw = canWithdraw;
        this.canDeposit = canDeposit;
    }

    public boolean canWithdraw() {
        return canWithdraw;
    }

    public boolean canDeposit() {
        return canDeposit;
    }
}
