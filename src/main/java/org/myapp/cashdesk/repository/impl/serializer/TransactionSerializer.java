package org.myapp.cashdesk.repository.impl.serializer;

import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.exception.CashDeskParseException;
import org.myapp.cashdesk.exception.CashDeskSerializationException;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.transaction.OperationType;
import org.myapp.cashdesk.model.transaction.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

import static java.util.Objects.isNull;

/**
 * This class is responsible for transaction serialization and parsing.
 */
@Slf4j
public class TransactionSerializer extends BaseFileSerializer<Transaction> {
    private static final int NUMBER_OF_TRANSACTION_FIELDS = 8;
    private static final int TRANSACTION_CURRENCY_INDEX = 4;
    private static final int TRANSACTION_ID_INDEX = 0;
    private static final int CASHIER_ID_INDEX = 1;
    private static final int CASHIER_NAME_INDEX = 2;
    private static final int OPERATION_TYPE_INDEX = 3;
    private static final int TRANSACTION_AMOUNT_INDEX = 5;
    private static final int TRANSACTION_DENOMINATION_INDEX = 6;
    private static final int NEW_CASHIER_BALANCE_INDEX = 7;
    private static final int TRANSACTION_TIMESTAMP_INDEX = 8;

    /**
     * Serializes transaction.
     *
     * @param transaction transaction to be serialized
     * @return serialized transaction
     * @throws CashDeskSerializationException if the given transaction is null
     */
    @Override
    public String serialize(final Transaction transaction) {
        if (isNull(transaction)) {
            throw new CashDeskSerializationException("Failed to serialize transaction! Cashier can not be null!");
        }
        log.info("Serializing transaction");
        try {
            return joinFields(
                    transaction.getId(),
                    String.valueOf(transaction.getCashierId()),
                    transaction.getCashierName(),
                    transaction.getType().name(),
                    transaction.getCurrency().name(),
                    transaction.getAmount().stripTrailingZeros().toPlainString(),
                    joinDenominations(transaction.getDenominations()),
                    serializeBalancesMap(transaction.getNewCashierBalances()),
                    transaction.getTimestamp().toString()
            );
        } catch (CashDeskSerializationException e) {
            throw e;
        }catch (Exception e) {
            throw new CashDeskSerializationException("Could not serialize transaction", e);
        }
    }

    /**
     * Parses transaction from line.
     *
     * @param line line to be parsed
     * @return parsed transaction
     * @throws CashDeskParseException if the given line is null
     */
    @Override
    public Transaction parse(final String line) {
        checkIfLineIsNull(line);
        log.info("Parsing transaction from line: {}", line);

        String[] parts = splitFields(line);
        validateCorrectNumberOfFields(NUMBER_OF_TRANSACTION_FIELDS, parts.length, line);

        try {
            Currency currency = Currency.valueOf(parts[TRANSACTION_CURRENCY_INDEX]);
            return new Transaction(
                    parts[TRANSACTION_ID_INDEX],
                    Long.parseLong(parts[CASHIER_ID_INDEX]),
                    parts[CASHIER_NAME_INDEX],
                    OperationType.valueOf(parts[OPERATION_TYPE_INDEX]),
                    currency,
                    new BigDecimal(parts[TRANSACTION_AMOUNT_INDEX]),
                    parseDenominations(parts[TRANSACTION_DENOMINATION_INDEX], currency),
                    parseBalances(parts[NEW_CASHIER_BALANCE_INDEX]),
                    Instant.parse(parts[TRANSACTION_TIMESTAMP_INDEX])
            );
        } catch (Exception e) {
            throw new CashDeskParseException("Failed to parse transaction from line: " + line, e);
        }
    }
}