package org.myapp.cashdesk.repository.serializer;

import org.myapp.cashdesk.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionSerializer extends BaseFileSerializer<Transaction> {
    @Override
    public String serialize(final Transaction transaction) {
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
    }

    @Override
    public Transaction parse(final String line) {
        String[] parts = splitFields(line);
        if (parts.length < 8) return null;

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
            return null;
        }
    }
}