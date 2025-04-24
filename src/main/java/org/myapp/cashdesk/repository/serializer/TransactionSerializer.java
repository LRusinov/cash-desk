package org.myapp.cashdesk.repository.serializer;

import org.myapp.cashdesk.exception.CashDeskParseException;
import org.myapp.cashdesk.exception.CashDeskSerializationException;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.transaction.OperationType;
import org.myapp.cashdesk.model.transaction.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionSerializer extends BaseFileSerializer<Transaction> {

    private static final int NUMBER_OF_TRANSACTION_FIELDS = 8;

    @Override
    public String serialize(final Transaction transaction) {
        try {
            return joinFields(
                    transaction.getId(),
                    String.valueOf(transaction.getCashierId()),
                    transaction.getCashierName(),
                    transaction.getType().name(),
                    transaction.getCurrency().name(),
                    transaction.getAmount().stripTrailingZeros().toPlainString(),
                    joinDenominations(transaction.getDenominations()),
                    transaction.getTimestamp().toString()
            );
        } catch (CashDeskSerializationException e) {
            throw e;
        }catch (Exception e) {
            throw new CashDeskSerializationException("Could not serialize transaction", e);
        }
    }

    @Override
    public Transaction parse(final String line) {
        String[] parts = splitFields(line);
        validateCorrectNumberOfFields(NUMBER_OF_TRANSACTION_FIELDS, parts.length, line);

        try {
            Currency currency = Currency.valueOf(parts[4]);
            return new Transaction(
                    parts[0],
                    Long.parseLong(parts[1]),
                    parts[2],
                    OperationType.valueOf(parts[3]),
                    currency,
                    new BigDecimal(parts[5]),
                    parseDenominations(parts[6], currency),
                    LocalDateTime.parse(parts[7])
            );
        } catch (Exception e) {
            throw new CashDeskParseException("Failed to transaction from line: " + line, e);
        }
    }
}