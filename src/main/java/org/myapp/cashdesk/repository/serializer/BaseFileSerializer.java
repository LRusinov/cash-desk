package org.myapp.cashdesk.repository.serializer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.myapp.cashdesk.exception.CashDeskParseException;
import org.myapp.cashdesk.exception.CashDeskSerializationException;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.myapp.cashdesk.utils.DenominationUtils.getDenomination;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseFileSerializer<T> implements FileSerializer<T> {
    private static final String FIELD_DELIMITER_REGEX = "\\|";
    private static final String FIELD_DELIMITER = "|";
    private static final String ITEM_DELIMITER = ",";
    private static final String DENOMINATION_DELIMITER = "x";
    private static final String BALANCE_DELIMITER = "->";
    private static final String CURRENCY_DELIMITER = ";";
    private static final String EMPTY_STRING = "";
    private static final int BALANCE_AMOUNT_INDEX = 0;
    private static final int BALANCE_DENOMINATION_INDEX = 1;
    private static final int NUMBER_OF_BALANCE_FIELDS = 2;


    protected String joinFields(final String... fields) {
        return String.join(FIELD_DELIMITER, fields);
    }

    protected String[] splitFields(final String line) {
        return line.split(FIELD_DELIMITER_REGEX);
    }

    protected String joinDenominations(final Map<Denomination, Integer> denominations) {
        return denominations.entrySet().stream()
                .map(e -> e.getValue() + DENOMINATION_DELIMITER + e.getKey().getValue())
                .collect(Collectors.joining(ITEM_DELIMITER));
    }

    protected Map<Denomination, Integer> parseDenominations(String denominationsStr, Currency currency) {
        if (denominationsStr.isEmpty()) return Map.of();

        return Arrays.stream(denominationsStr.split(ITEM_DELIMITER))
                .map(s -> s.split(DENOMINATION_DELIMITER))
                .collect(Collectors.toMap(
                        parts -> getDenomination(currency, new BigDecimal(parts[1])),
                        parts -> Integer.parseInt(parts[0])
                ));
    }

    protected String serializeBalancesMap(final Map<Currency, Balance> balances) {
        if (isNull(balances) || balances.isEmpty()) {
            throw new CashDeskSerializationException("Cashier must have balance for at least one currency!");
        }

        try {
            return balances.entrySet().stream()
                    .map(entry -> serializeBalance(entry.getValue(), entry.getKey()))
                    .collect(Collectors.joining(CURRENCY_DELIMITER));
        } catch (Exception e) {
            throw new CashDeskSerializationException("Failed to serialize balances", e);
        }
    }

    private String serializeBalance(final Balance balance, final Currency currency) {
        try {
            return balance.getTotalAmount() +
                    currency.name() +
                    BALANCE_DELIMITER +
                    joinDenominations(balance.getDenominations());
        } catch (Exception e) {
            throw new CashDeskSerializationException(
                    String.format("Failed to serialize balance for currency %s", currency), e);
        }
    }

    protected Map.Entry<Currency, Balance> parseBalanceEntry(final String balanceEntry) {

        String[] parts = balanceEntry.split(BALANCE_DELIMITER, 2);
        validateCorrectFormat(NUMBER_OF_BALANCE_FIELDS, parts.length, "{amount}{CURRENCY}->{denominations}", balanceEntry);

        String amountPart = parts[BALANCE_AMOUNT_INDEX];
        Currency currency = extractCurrency(amountPart);
        if (isNull(currency)) {
            throw new CashDeskParseException("Unknown currency in amount: " + amountPart);
        }

        String amountStr = amountPart.replace(currency.name(), EMPTY_STRING);
        BigDecimal total = new BigDecimal(amountStr);

        Map<Denomination, Integer> denominations = parseDenominations(parts[BALANCE_DENOMINATION_INDEX], currency);
        return Map.entry(currency, new Balance(total, denominations));
    }

    protected void validateCorrectNumberOfFields(final int expectedNumberOfFields, final int partsLength, final String line) {
        if (partsLength < expectedNumberOfFields) {
            throw new CashDeskParseException(
                    String.format("Invalid line format! Expected at least %d fields to be parsed but got %d in line: '%s'",
                            expectedNumberOfFields, partsLength, line)
            );
        }
    }

    protected void validateCorrectFormat(final int expectedNumberOfFields, final int partsLength, final String expectedFormat, final String actual) {
        if (partsLength < expectedNumberOfFields) {
            throw new CashDeskParseException(String.format("Invalid line format! The expected format is '%s' but got '%s'", expectedFormat, actual)
            );
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

    protected Map<Currency, Balance> parseBalances(final String balancesStr) {
        if (isNull(balancesStr) || balancesStr.isEmpty()) {
            throw new CashDeskParseException("Cashier must have balance for at least one currency!");
        }

        try {
            return Arrays.stream(balancesStr.split(CURRENCY_DELIMITER))
                    .map(this::parseBalanceEntry)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));
        } catch (Exception e) {
            throw new CashDeskParseException("Failed to parse balances: " + balancesStr, e);
        }
    }
}