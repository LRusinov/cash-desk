package org.myapp.cashdesk.repository.serializer;

import org.myapp.cashdesk.model.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class CashierSerializer extends BaseFileSerializer<Cashier> {
    private static final String BALANCE_DELIMITER = "->";
    private static final String CURRENCY_DELIMITER = ";";
    public static final String EMPTY_STRING = "";

    @Override
    public String serialize(final Cashier cashier) {
        return joinFields(
                String.valueOf(cashier.getId()),
                cashier.getName(),
                serializeBalancesMap(cashier.getBalance())
        );
    }

    @Override
    public Cashier parse(final String line) {
        String[] parts = splitFields(line);
        if (parts.length < 3) return null;

        try {
            return new Cashier(
                    Long.parseLong(parts[0]),
                    parts[1],
                    parseBalances(parts[2])
            );
        } catch (Exception e) {
            return null;
        }
    }

    private String serializeBalancesMap(final Map<Currency, Balance> balances) {
        if (isNull(balances) || balances.isEmpty()) {
            return EMPTY_STRING;
        }

        return balances.entrySet().stream()
                .map(entry -> serializeBalance(entry.getValue(), entry.getKey()))
                .collect(Collectors.joining(CURRENCY_DELIMITER));
    }

    private String serializeBalance(final Balance balance, final Currency currency) {
        if (isNull(balance)) return EMPTY_STRING;

        return balance.getTotalAmount().stripTrailingZeros().toPlainString() +
                currency.name() +
                BALANCE_DELIMITER +
                joinDenominations(balance.getDenominations());
    }

    private Map<Currency, Balance> parseBalances(final String balancesStr) {
        if (isNull(balancesStr) || balancesStr.isEmpty()) {
            return Map.of();
        }

        return Arrays.stream(balancesStr.split(CURRENCY_DELIMITER))
                .map(this::parseBalanceEntry)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    private Map.Entry<Currency, Balance> parseBalanceEntry(final String balanceEntry) {
        try {
            String[] parts = balanceEntry.split(BALANCE_DELIMITER, 2);
            if (parts.length != 2) return null;

            String amountPart = parts[0];
            Currency currency = extractCurrency(amountPart);
            if (isNull(currency)) return null;

            String amountStr = amountPart.replace(currency.name(), EMPTY_STRING);
            BigDecimal total = new BigDecimal(amountStr);

            Map<Denomination, Integer> denominations = parseDenominations(parts[1], currency);
            return Map.entry(currency, new Balance(denominations, total));
        } catch (Exception e) {
            return null;
        }
    }

    private Currency extractCurrency(final String amountStr) {
        for (Currency currency : Currency.values()) {
            if (amountStr.contains(currency.name())) {
                return currency;
            }
        }
        return null;
    }
}
