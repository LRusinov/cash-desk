package org.myapp.cashdesk.repository.impl.serializer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.myapp.cashdesk.exception.CashDeskParseException;
import org.myapp.cashdesk.exception.CashDeskSerializationException;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.transaction.OperationType;
import org.myapp.cashdesk.model.transaction.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TransactionSerializerTest {

    private final TransactionSerializer serializer = new TransactionSerializer();

    private static final String TRANSACTION_ID = "TX_1745573223040";
    private static final long CASHIER_ID = 2L;
    private static final String CASHIER_NAME = "PETER";
    private static final OperationType OPERATION_TYPE = OperationType.DEPOSIT;
    private static final Currency CURRENCY = Currency.EUR;
    private static final BigDecimal AMOUNT = new BigDecimal("100");
    private static final Instant TIMESTAMP = Instant.parse("2025-04-25T09:27:03.040078800Z");

    @Test
    void serialize_shouldThrowExceptionWhenTransactionIsNull() {
        assertThrows(CashDeskSerializationException.class,
                () -> serializer.serialize(null), "Could not serialize transaction");
    }

    @Test
    void parse_shouldCorrectlyParseTransactionLine() {
        String transactionLine = "TX_1745573223040|2|PETER|DEPOSIT|EUR|100|1x50,5x10|1000BGN->10x50,50x10;2600EUR->130x10,26x50|2025-04-25T09:27:03.040078800Z";

        Transaction result = serializer.parse(transactionLine);

        assertNotNull(result);
        assertEquals(TRANSACTION_ID, result.getId());
        assertEquals(CASHIER_ID, result.getCashierId());
        assertEquals(CASHIER_NAME, result.getCashierName());
        assertEquals(OPERATION_TYPE, result.getType());
        assertEquals(CURRENCY, result.getCurrency());
        assertEquals(AMOUNT, result.getAmount());
        assertEquals(TIMESTAMP, result.getTimestamp());
    }

    @ParameterizedTest
    @MethodSource
    void parse_shouldThrowExceptionWhenLineIsInvalid(String line) {
        assertThrows(CashDeskParseException.class, () -> serializer.parse(line));
    }

    private static Stream<Arguments> parse_shouldThrowExceptionWhenLineIsInvalid() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("TX_1745573223040"),
                Arguments.of("TX_1745573223040|2|PETER|DEPOSIT|EUR|100|1x50,5x10|1000BGN->10x50,50x10"),
                Arguments.of("TX_1745573223040|invalid|PETER|DEPOSIT|EUR|100|1x50,5x10|1000BGN->10x50,50x10;2600EUR->130x10,26x50|2025-04-25T09:27:03.040078800Z"),
                Arguments.of("TX_1745573223040|2|PETER|INVALID_OP|EUR|100|1x50,5x10|1000BGN->10x50,50x10;2600EUR->130x10,26x50|2025-04-25T09:27:03.040078800Z"),
                Arguments.of("TX_1745573223040|2|PETER|DEPOSIT|INVALID|100|1x50,5x10|1000BGN->10x50,50x10;2600EUR->130x10,26x50|2025-04-25T09:27:03.040078800Z"),
                Arguments.of("TX_1745573223040|2|PETER|DEPOSIT|EUR|invalid|1x50,5x10|1000BGN->10x50,50x10;2600EUR->130x10,26x50|2025-04-25T09:27:03.040078800Z"),
                Arguments.of("TX_1745573223040|2|PETER|DEPOSIT|EUR|100|invalid|1000BGN->10x50,50x10;2600EUR->130x10,26x50|2025-04-25T09:27:03.040078800Z"),
                Arguments.of("TX_1745573223040|2|PETER|DEPOSIT|EUR|100|1x50,5x10|invalid|2025-04-25T09:27:03.040078800Z"),
                Arguments.of("TX_1745573223040|2|PETER|DEPOSIT|EUR|100|1x50,5x10|1000BGN->10x50,50x10;2600EUR->130x10,26x50|invalid")
        );
    }
}