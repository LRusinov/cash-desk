package org.myapp.cashdesk.model.denomination;

public enum Currency {
    BGN, EUR;

    public static Currency fromStringAlternative(String value) {
        for (Currency currency : Currency.values()) {
            if (currency.name().equalsIgnoreCase(value)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Invalid currency: " + value);
    }
}
