package org.myapp.cashdesk.utils;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.myapp.cashdesk.model.denomination.BgnDenomination;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;
import org.myapp.cashdesk.model.denomination.EurDenomination;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Denomination utils class which contains common denomination methods.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DenominationUtils {

    /**
     * Finds denomination constant by given value.
     *
     * @param currency currency whose constant is searched for
     * @param value target value
     * @return denomination constant
     * @throws IllegalArgumentException if value is null
     */
    public static Denomination getDenomination(final Currency currency, final BigDecimal value) {
        return switch (currency) {
            case BGN -> BgnDenomination.findByValue(value);
            case EUR -> EurDenomination.findByValue(value);
        };
    }

    public static Map<BigDecimal, @NotNull Integer> convertAllKeysToBigDecimal(final Map<Denomination, Integer> balance) {
        return balance.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        k -> k.getKey().getValue(), Map.Entry::getValue));
    }
}
