package org.myapp.cashdesk.repository.serializer;

import org.myapp.cashdesk.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class TransactionSerializer extends BaseFileSerializer<Transaction> {

    public TransactionSerializer() {
        super();
    }

    @Override
    public String serialize(Transaction transaction) {
        return joinFields(
                String.valueOf(transaction.getId()),
                String.valueOf(transaction.getCashierId()),
                transaction.getType().name(),
                transaction.getCurrency().name(),
                transaction.getAmount().stripTrailingZeros().toPlainString(),
                joinDenominations(transaction.getDenominations()),
                transaction.getTimestamp().toString()
        );
    }

    @Override
    public Transaction parse(String line) {
        String[] parts = splitFields(line);
        if (parts.length < 7) return null;

        try {
            Currency currency = Currency.valueOf(parts[3]);
            return new Transaction(
                    Long.parseLong(parts[0]),
                    Long.parseLong(parts[1]),
                    TransactionType.valueOf(parts[2]),
                    currency,
                    new BigDecimal(parts[4]),
                    parseDenominationsByCurrency(parts[5], currency),
                    LocalDateTime.parse(parts[6])
            );
        } catch (Exception e) {
            return null;
        }
    }

    private Map<? extends Denomination, Integer> parseDenominationsByCurrency(
            String itemsStr,
            Currency currency) {

        if (currency == Currency.BGN) {
            return parseDenominations(itemsStr, BgnDenomination.class);
        } else {
            return parseDenominations(itemsStr, EurDenomination.class);
        }
    }
}