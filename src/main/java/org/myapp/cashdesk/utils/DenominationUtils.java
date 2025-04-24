package org.myapp.cashdesk.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.myapp.cashdesk.model.denomination.BgnDenomination;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;
import org.myapp.cashdesk.model.denomination.EurDenomination;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DenominationUtils {
    public static Denomination getDenomination(Currency currency, BigDecimal value) {
        return switch (currency) {
            case BGN -> BgnDenomination.findByValue(value);
            case EUR -> EurDenomination.findByValue(value);
        };
    }
}
