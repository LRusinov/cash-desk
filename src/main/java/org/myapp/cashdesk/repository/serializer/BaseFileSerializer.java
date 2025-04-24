package org.myapp.cashdesk.repository.serializer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.myapp.cashdesk.exception.CashDeskParseException;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;
import org.myapp.cashdesk.model.denomination.DenominationFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseFileSerializer<T> implements FileSerializer<T> {
    private static final String FIELD_DELIMITER = "|";
    private static final String ITEM_DELIMITER = ",";
    private static final String DENOMINATION_DELIMITER = "x";

    protected String joinFields(final String... fields) {
        return String.join(FIELD_DELIMITER, fields);
    }

    protected String[] splitFields(final String line) {
        return line.split(FIELD_DELIMITER);
    }

    protected String joinDenominations(final Map<Denomination, Integer> denominations) {
        return denominations.entrySet().stream()
                .map(e -> e.getValue() + DENOMINATION_DELIMITER + e.getKey().getValue().stripTrailingZeros().toPlainString())
                .collect(Collectors.joining(ITEM_DELIMITER));
    }

    protected Map<Denomination, Integer> parseDenominations(String denominationsStr, Currency currency) {
        if (denominationsStr.isEmpty()) return Map.of();

        return Arrays.stream(denominationsStr.split(ITEM_DELIMITER))
                .map(s -> s.split(DENOMINATION_DELIMITER))
                .collect(Collectors.toMap(
                        parts -> DenominationFactory.createDenomination(currency, new BigDecimal(parts[1])),
                        parts -> Integer.parseInt(parts[0])
                ));
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
}