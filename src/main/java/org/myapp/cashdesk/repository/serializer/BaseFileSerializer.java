package org.myapp.cashdesk.repository.serializer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;
import org.myapp.cashdesk.model.denomination.DenominationFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseFileSerializer<T> implements FileSerializer<T> {
    protected static final String FIELD_DELIMITER = "|";
    protected static final String ITEM_DELIMITER = ",";
    protected static final String DENOMINATION_DELIMITER = "x";

    protected String joinFields(final String... fields) {
        return String.join(FIELD_DELIMITER, fields);
    }

    protected String[] splitFields(final String line) {
        return line.split(FIELD_DELIMITER);
    }

    protected <E extends Denomination> String joinDenominations(final Map<E, Integer> denominations) {
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
}