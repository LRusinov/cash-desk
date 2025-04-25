package org.myapp.cashdesk.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.myapp.cashdesk.model.denomination.BgnDenomination;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;
import org.myapp.cashdesk.model.denomination.EurDenomination;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.myapp.cashdesk.utils.DenominationUtils.*;

class DenominationUtilsTest {

    @ParameterizedTest
    @MethodSource
    void getDenomination_shouldReturnCorrectDenominationForCurrency(Currency currency, BigDecimal value, Denomination expected) {
        Denomination result = getDenomination(currency, value);
        assertEquals(expected, result);
    }

    @Test
    void getDenomination_shouldThrowForInvalidValue() {
        assertThrows(IllegalArgumentException.class,
                () -> getDenomination(Currency.EUR, BigDecimal.valueOf(3.14)));
    }

    @Test
    void convertAllKeysToBigDecimal_shouldConvertDenominationMapCorrectly() {
        Map<Denomination, Integer> input = Map.of(
                EurDenomination.FIFTY_EUROS, 10,
                EurDenomination.TWENTY_EUROS, 5,
                BgnDenomination.TEN_LEVA, 20
        );

        Map<BigDecimal, Integer> result = convertAllKeysToBigDecimal(input);

        assertEquals(3, result.size());
        assertEquals(10, result.get(EurDenomination.FIFTY_EUROS.getValue()));
        assertEquals(5, result.get(EurDenomination.TWENTY_EUROS.getValue()));
        assertEquals(20, result.get(BgnDenomination.TEN_LEVA.getValue()));
    }

    @Test
    void convertAllKeysToBigDecimal_shouldReturnUnmodifiableMap() {
        Map<Denomination, Integer> input = Map.of(EurDenomination.FIFTY_EUROS, 10);
        Map<BigDecimal, Integer> result = convertAllKeysToBigDecimal(input);

        assertThrows(UnsupportedOperationException.class, () -> result.put(BigDecimal.TEN, 5));
    }

    @Test
    void convertAllKeysToBigDecimal_shouldHandleEmptyMap() {
        Map<Denomination, Integer> input = Map.of();
        Map<BigDecimal, Integer> result = convertAllKeysToBigDecimal(input);

        assertTrue(result.isEmpty());
    }

    private static Stream<Arguments> getDenomination_shouldReturnCorrectDenominationForCurrency() {
        return Stream.of(
                Arguments.of(Currency.EUR, EurDenomination.FIFTY_EUROS.getValue(), EurDenomination.FIFTY_EUROS),
                Arguments.of(Currency.EUR, EurDenomination.TWENTY_EUROS.getValue(), EurDenomination.TWENTY_EUROS),
                Arguments.of(Currency.BGN, BgnDenomination.TEN_LEVA.getValue(), BgnDenomination.TEN_LEVA),
                Arguments.of(Currency.BGN, BgnDenomination.FIVE_LEVA.getValue(), BgnDenomination.FIVE_LEVA)
        );
    }
}