package org.myapp.cashdesk.repository.serializer;

import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.exception.CashDeskParseException;
import org.myapp.cashdesk.exception.CashDeskSerializationException;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
public class CashierSerializer extends BaseFileSerializer<Cashier> {
    private static final String BALANCE_DELIMITER = "->";
    private static final String CURRENCY_DELIMITER = ";";
    private static final String EMPTY_STRING = "";
    private static final int NUMBER_OF_CASHIER_FIELDS = 3;
    private static final int CASHIER_ID_INDEX = 0;
    private static final int CASHIER_NAME_INDEX = 1;
    private static final int CASHIER_BALANCE_INDEX = 2;
    private static final int NUMBER_OF_BALANCE_FIELDS = 2;
    private static final int BALANCE_AMOUNT_INDEX = 0;
    private static final int BALANCE_DENOMINATION_INDEX = 1;

    @Override
    public String serialize(final Cashier cashier) {
        if (isNull(cashier)) {
            throw new CashDeskSerializationException("Failed to serialize cashier! Cashier can not be null!");
        }
        log.info("Serializing cashier with id: {}", cashier.getId());
        return joinFields(
                String.valueOf(cashier.getId()),
                cashier.getName(),
                serializeBalancesMap(cashier.getBalance())
        );
    }

    @Override
    public Cashier parse(final String line) {
        if (isNull(line)) {
            throw new CashDeskParseException("Input line cannot be null!");
        }

        String[] parts = splitFields(line);
        validateCorrectNumberOfFields(NUMBER_OF_CASHIER_FIELDS, parts.length, line);

        try {
            return new Cashier(
                    Long.parseLong((parts[CASHIER_ID_INDEX])),
                    parts[CASHIER_NAME_INDEX],
                    parseBalances(parts[CASHIER_BALANCE_INDEX])
            );
        } catch (Exception e) {
            throw new CashDeskParseException("Failed to parse cashier from line: " + line, e);
        }
    }


    private String serializeBalancesMap(final Map<Currency, Balance> balances) {
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
            return balance.getTotalAmount().stripTrailingZeros().toPlainString() +
                    currency.name() +
                    BALANCE_DELIMITER +
                    joinDenominations(balance.getDenominations());
        } catch (Exception e) {
            throw new CashDeskSerializationException(
                    String.format("Failed to serialize balance for currency %s", currency), e);
        }
    }

    private Map<Currency, Balance> parseBalances(final String balancesStr) {
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

    private Map.Entry<Currency, Balance> parseBalanceEntry(final String balanceEntry) {

        String[] parts = balanceEntry.split(BALANCE_DELIMITER, 2);
        validateCorrectFormat(NUMBER_OF_BALANCE_FIELDS, parts.length, "{amount}{CURRENCY}->{denominations}", balanceEntry);

        String amountPart = parts[BALANCE_AMOUNT_INDEX];
        Currency currency = extractCurrency(amountPart);
        if (isNull(currency)) {
            throw new CashDeskParseException("Unknown currency in amount: " + amountPart);
        }

        String amountStr = amountPart.replace(currency.name(), EMPTY_STRING);
        BigDecimal total = new BigDecimal(amountStr);

        try {
            Map<Denomination, Integer> denominations = parseDenominations(parts[BALANCE_DENOMINATION_INDEX], currency);
            return Map.entry(currency, new Balance(denominations, total));
        }catch (Exception e) {
            throw new CashDeskParseException("Failed to parse balance entry: " + balanceEntry, e);
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
