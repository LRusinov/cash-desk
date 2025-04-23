package org.myapp.cashdesk.repository.serializer;

import org.myapp.cashdesk.model.Denomination;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseFileSerializer<T> implements FileSerializer<T> {
    protected static final String FIELD_DELIMITER = "\\|";
    protected static final String ITEM_DELIMITER = ",";
    protected static final String DENOMINATION_DELIMITER = "x";

    protected BaseFileSerializer() {}

    protected String joinFields(String... fields) {
        return String.join(FIELD_DELIMITER, fields);
    }

    protected String[] splitFields(String line) {
        return line.split(FIELD_DELIMITER);
    }

    protected <E extends Denomination> String joinDenominations(Map<E, Integer> denominations) {
        return denominations.entrySet().stream()
                .map(e -> e.getValue() + DENOMINATION_DELIMITER + e.getKey().getValue())
                .collect(Collectors.joining(ITEM_DELIMITER));
    }

    protected <E extends Enum<E> & Denomination> Map<E, Integer> parseDenominations(String itemsStr, Class<E> enumClass) {
        return Arrays.stream(itemsStr.split(ITEM_DELIMITER))
                .map(s -> s.split(DENOMINATION_DELIMITER))
                .collect(Collectors.toMap(
                        parts -> Denomination.Lookup.fromValue(new BigDecimal(parts[1]), enumClass),
                        parts -> Integer.parseInt(parts[0])
                ));
    }
}