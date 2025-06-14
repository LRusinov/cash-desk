package org.myapp.cashdesk.model.denomination;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Denomination interface containing denomination's main methods
 */
public interface Denomination {
    BigDecimal getValue();

    Currency getCurrency();

    final class Lookup {
        private Lookup() {
        }

        public static <E extends Enum<E> & Denomination> E fromValue(
                BigDecimal value,
                Class<E> enumClass) {
            return Arrays.stream(enumClass.getEnumConstants())
                    .filter(e -> e.getValue().compareTo(value) == 0)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No enum constant was found for value: " + value));
        }
    }
}