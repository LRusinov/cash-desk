package org.myapp.cashdesk.repository.impl.serializer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myapp.cashdesk.exception.CashDeskParseException;
import org.myapp.cashdesk.exception.CashDeskSerializationException;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.model.denomination.BgnDenomination;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.EurDenomination;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CashierSerializerTest {

    @InjectMocks
    private CashierSerializer cashierSerializer;

    @Test
    void serialize_shouldThrowExceptionWhenCashierIsNull() {
        assertThrows(CashDeskSerializationException.class,
                () -> cashierSerializer.serialize(null), "Failed to serialize cashier! Cashier can not be null!");
    }

    @Test
    void serialize_shouldCorrectlySerializeCashier() {
        Map<Currency, Balance> testBalance =
                Map.of(Currency.EUR, new Balance(BigDecimal.valueOf(1000), Map.of(EurDenomination.TEN_EUROS, 100)),
                        Currency.BGN, new Balance(BigDecimal.valueOf(1000), Map.of(BgnDenomination.TWENTY_LEVA, 50)));
        Cashier cashier = new Cashier(1L, "Lyubomir R", testBalance);
        String result = cashierSerializer.serialize(cashier);

        final String expectedSerializedLine = "1|Lyubomir R|1000BGN->50x20;1000EUR->100x10";
        assertEquals(expectedSerializedLine, result);
    }

    @Test
    void parse_shouldThrowExceptionWhenLineIsNull() {
        assertThrows(CashDeskParseException.class,
                () -> cashierSerializer.parse(null), "Input line cannot be null!");
    }

    @Test
    void parse_shouldThrowExceptionWhenFieldsCountIsInvalid() {
        String invalidLine = "1|Ivan";
        assertThrows(CashDeskParseException.class,
                () -> cashierSerializer.parse(invalidLine),
                "Expected 3 fields but got 2 in line: " + invalidLine);
    }

    @Test
    void parse_shouldThrowExceptionWhenIdIsNotNumber() {
        String invalidLine = "abv|Georgi|1000BGN->50x20;1000EUR->50x20";
        assertThrows(CashDeskParseException.class,
                () -> cashierSerializer.parse(invalidLine), "Failed to parse cashier from line: " + invalidLine);
    }

    @Test
    void parse_shouldCorrectlyParseValidLine() {
        String validLine = "1|Lyubomir R|1000BGN->50x20;1000EUR->50x20";
        Cashier result = cashierSerializer.parse(validLine);
        Map<Currency, Balance> expectedBalance = Map.of(Currency.EUR, new Balance(BigDecimal.valueOf(1000), Map.of(EurDenomination.TWENTY_EUROS, 50)),
                Currency.BGN, new Balance(BigDecimal.valueOf(1000), Map.of(BgnDenomination.TWENTY_LEVA, 50)));
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Lyubomir R", result.getName());
        assertEquals(2, result.getBalance().size());
        assertEquals(expectedBalance.get(Currency.EUR), result.getBalance().get(Currency.EUR));
        assertEquals(expectedBalance.get(Currency.BGN), result.getBalance().get(Currency.BGN));
    }

    @Test
    void parseAndSerialize_shouldBeReversibleOperations() {
        Map<Currency, Balance> testBalance =
                Map.of(Currency.EUR, new Balance(BigDecimal.valueOf(1000), Map.of(EurDenomination.TWENTY_EUROS, 50)),
                        Currency.BGN, new Balance(BigDecimal.valueOf(1000), Map.of(BgnDenomination.TWENTY_LEVA, 50)));
        Cashier original = new Cashier(1L, "Georgi Petrov", testBalance);

        String serialized = cashierSerializer.serialize(original);
        Cashier parsed = cashierSerializer.parse(serialized);

        assertEquals(original.getId(), parsed.getId());
        assertEquals(original.getName(), parsed.getName());
        assertEquals(original.getBalance(), parsed.getBalance());
    }
}