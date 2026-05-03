package com.my.commandservice.entity.enumeration;

public enum AccountType {

    SAVINGS("Savings Account", true),
    CURRENT("Current Account", false),
    BUSINESS("Business Account", false),
    FIXED_DEPOSIT("Fixed Deposit", true),
    SYSTEM("System Account", false);

    private final String description;
    private final boolean earnsInterest;

    AccountType(String description, boolean earnsInterest) {
        this.description = description;
        this.earnsInterest = earnsInterest;
    }

    public String getDescription() {
        return description;
    }

    public boolean earnsInterest() {
        return earnsInterest;
    }
}
