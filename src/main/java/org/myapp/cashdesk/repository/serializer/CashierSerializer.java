package org.myapp.cashdesk.repository.serializer;

import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.exception.CashDeskParseException;
import org.myapp.cashdesk.exception.CashDeskSerializationException;
import org.myapp.cashdesk.model.cashier.Cashier;

import static java.util.Objects.isNull;

@Slf4j
public class CashierSerializer extends BaseFileSerializer<Cashier> {
    private static final int NUMBER_OF_CASHIER_FIELDS = 3;
    private static final int CASHIER_ID_INDEX = 0;
    private static final int CASHIER_NAME_INDEX = 1;
    private static final int CASHIER_BALANCE_INDEX = 2;


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
}
