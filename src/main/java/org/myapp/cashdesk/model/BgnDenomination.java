package org.myapp.cashdesk.model;

import java.math.BigDecimal;

public enum BgnDenomination implements Denomination {
    // Coins
    ONE_STOTINKA(BigDecimal.valueOf(0.01)),
    TWO_STOTINKI(BigDecimal.valueOf(0.02)),
    FIVE_STOTINKI(BigDecimal.valueOf(0.05)),
    TEN_STOTINKI(BigDecimal.valueOf(0.10)),
    TWENTY_STOTINKI(BigDecimal.valueOf(0.20)),
    FIFTY_STOTINKI(BigDecimal.valueOf(0.50)),

    // Banknotes
    ONE_LEV(BigDecimal.valueOf(1.00)),
    TWO_LEVA(BigDecimal.valueOf(2.00)),
    FIVE_LEVA(BigDecimal.valueOf(5.00)),
    TEN_LEVA(BigDecimal.valueOf(10.00)),
    TWENTY_LEVA(BigDecimal.valueOf(20.00)),
    FIFTY_LEVA(BigDecimal.valueOf(50.00));

    private final BigDecimal value;

    BgnDenomination(final BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public Currency getCurrency() {
        return Currency.BGN;
    }
}
