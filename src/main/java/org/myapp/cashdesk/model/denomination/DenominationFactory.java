package org.myapp.cashdesk.model.denomination;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DenominationFactory {
    public static Denomination createDenomination(final Currency currency, final BigDecimal value) {
        try {
            Class<? extends Denomination> cls = DenominationRegistry.getDenominationClass(currency);
            Constructor<? extends Denomination> constructor = cls.getDeclaredConstructor(BigDecimal.class);
            return constructor.newInstance(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create denomination for " + currency, e);
        }
    }
}
