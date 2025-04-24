package org.myapp.cashdesk.model.denomination;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum EurDenomination implements Denomination {
    // Coins
    ONE_CENT(new BigDecimal("0.01")),
    TWO_CENTS(new BigDecimal("0.02")),
    FIVE_CENTS(new BigDecimal("0.05")),
    TEN_CENTS(new BigDecimal("0.10")),
    TWENTY_CENTS(new BigDecimal("0.20")),
    FIFTY_CENTS(new BigDecimal("0.50")),
    ONE_EURO(new BigDecimal("1.00")),
    TWO_EUROS(new BigDecimal("2.00")),

    // Banknotes
    FIVE_EUROS(new BigDecimal("5.00")),
    TEN_EUROS(new BigDecimal("10.00")),
    TWENTY_EUROS(new BigDecimal("20.00")),
    FIFTY_EUROS(new BigDecimal("50.00")),
    ONE_HUNDRED_EUROS(new BigDecimal("100.00")),
    TWO_HUNDRED_EUROS(new BigDecimal("200.00")),
    FIVE_HUNDRED_EUROS(new BigDecimal("500.00"));

    private final BigDecimal value;

    EurDenomination(BigDecimal value) {
        this.value = value;
    }

    @Override
    public Currency getCurrency() {
        return Currency.EUR;
    }
}

