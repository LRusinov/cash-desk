package org.myapp.cashdesk.repository.serializer;

import org.myapp.cashdesk.model.*;

import java.math.BigDecimal;
import java.util.Map;

import static java.util.Objects.isNull;

public class CashierSerializer extends BaseFileSerializer<Cashier> {
    private static final String BALANCE_DELIMITER = ";";// No space between amount and currency
    public static final String EMPTY_STRING = "";

    @Override
    public String serialize(final Cashier cashier) {
        return joinFields(
                String.valueOf(cashier.getId()),
                cashier.getName(),
                serializeBalance(cashier.getBgnBalance(), Currency.BGN),
                serializeBalance(cashier.getEurBalance(), Currency.EUR)
        );
    }

    @Override
    public Cashier parse(final String line) {
        String[] parts = splitFields(line);
        if (parts.length < 4) return null;

        try {
            return new Cashier(
                    Long.parseLong(parts[0]),
                    parts[1],
                    parseBalance(parts[2], Currency.BGN),
                    parseBalance(parts[3], Currency.EUR)
            );
        } catch (Exception e) {
            return null;
        }
    }

    private String serializeBalance(final CurrencyBalance balance, final Currency currency) {
        if (isNull(balance)) return EMPTY_STRING;

        BigDecimal total = balance.getTotalAmount();
        String denominationsStr = joinDenominations(balance.getDenominations());

        return total.stripTrailingZeros().toPlainString() +
                currency.name() +
                BALANCE_DELIMITER +
                denominationsStr;
    }

    private CurrencyBalance parseBalance(final String balanceStr, final Currency currency) {
        if (isNull(balanceStr) || balanceStr.isEmpty()) return null;

        try {
            String[] parts = balanceStr.split(BALANCE_DELIMITER);
            if (parts.length != 2) return null;

            // Remove currency suffix from amount
            String amountStr = parts[0].replace(currency.name(), EMPTY_STRING);
            BigDecimal total = new BigDecimal(amountStr);

            Map<? extends Denomination, Integer> denominations;
            if (currency == Currency.BGN) {
                denominations = parseDenominations(parts[1], BgnDenomination.class);
            } else {
                denominations = parseDenominations(parts[1], EurDenomination.class);
            }

            return new CurrencyBalance(denominations, total);
        } catch (Exception e) {
            return null;
        }
    }
}