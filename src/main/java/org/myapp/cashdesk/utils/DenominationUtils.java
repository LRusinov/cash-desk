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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DenominationUtils {
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
