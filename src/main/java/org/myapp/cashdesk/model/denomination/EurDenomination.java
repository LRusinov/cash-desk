package org.myapp.cashdesk.model.denomination;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public enum EurDenomination implements Denomination {
    // Coins
    ONE_CENT(BigDecimal.valueOf(0.01)),
    TWO_CENTS(BigDecimal.valueOf(0.02)),
    FIVE_CENTS(BigDecimal.valueOf(0.05)),
    TEN_CENTS(BigDecimal.valueOf(0.10)),
    TWENTY_CENTS(BigDecimal.valueOf(0.20)),
    FIFTY_CENTS(BigDecimal.valueOf(0.50)),
    ONE_EURO(BigDecimal.valueOf(1)),
    TWO_EUROS(BigDecimal.valueOf(2)),

    // Banknotes
    FIVE_EUROS(BigDecimal.valueOf(5)),
    TEN_EUROS(BigDecimal.valueOf(10)),
    TWENTY_EUROS(BigDecimal.valueOf(20)),
    FIFTY_EUROS(BigDecimal.valueOf(50)),
    ONE_HUNDRED_EUROS(BigDecimal.valueOf(100)),
    TWO_HUNDRED_EUROS(BigDecimal.valueOf(200)),
    FIVE_HUNDRED_EUROS(BigDecimal.valueOf(500));

    @Getter
    private final BigDecimal value;

    private static final Map<BigDecimal,EurDenomination> valuesMap;
    static {
        valuesMap = new HashMap<>();
        for (EurDenomination v : EurDenomination.values()) {
            valuesMap.put(v.value, v);
        }
    }

    EurDenomination(BigDecimal value) {
        this.value = value;
    }

    public static EurDenomination findByValue(final BigDecimal value) {
        return valuesMap.get(value);
    }

    @Override
    public Currency getCurrency() {
        return Currency.EUR;
    }
}

