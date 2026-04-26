package com.my.commandservice.entity.enumeration;

public enum Currency {

    AZN("Azerbaijani Manat", "₼"),
    USD("US Dollar", "$"),
    EUR("Euro", "€"),
    TRY("Turkish Lira", "₺");

    private final String name;
    private final String symbol;

    Currency(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() { return name; }
    public String getSymbol() { return symbol; }
}