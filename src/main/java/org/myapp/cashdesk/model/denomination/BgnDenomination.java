package org.myapp.cashdesk.model.denomination;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public enum BgnDenomination implements Denomination {
    // Coins
    ONE_STOTINKA(BigDecimal.valueOf(0.01)),
    TWO_STOTINKI(BigDecimal.valueOf(0.02)),
    FIVE_STOTINKI(BigDecimal.valueOf(0.05)),
    TEN_STOTINKI(BigDecimal.valueOf(0.10)),
    TWENTY_STOTINKI(BigDecimal.valueOf(0.20)),
    FIFTY_STOTINKI(BigDecimal.valueOf(0.50)),

    // Banknotes
    ONE_LEV(BigDecimal.valueOf(1)),
    TWO_LEVA(BigDecimal.valueOf(2)),
    FIVE_LEVA(BigDecimal.valueOf(5)),
    TEN_LEVA(BigDecimal.valueOf(10)),
    TWENTY_LEVA(BigDecimal.valueOf(20)),
    FIFTY_LEVA(BigDecimal.valueOf(50)),
    HUNDRED_LEVA(BigDecimal.valueOf(100));

    @Getter
    private final BigDecimal value;

    private static final Map<BigDecimal,BgnDenomination> valuesMap;
    static {
        valuesMap = new HashMap<>();
        for (BgnDenomination v : BgnDenomination.values()) {
            valuesMap.put(v.value, v);
        }
    }

    BgnDenomination(final BigDecimal value) {
        this.value = value;
    }

    public static BgnDenomination findByValue(final BigDecimal value) {
        return valuesMap.get(value);
    }

    @Override
    public Currency getCurrency() {
        return Currency.BGN;
    }
}
