package org.myapp.cashdesk.model.cashier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.myapp.cashdesk.model.denomination.Currency;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Represents cashier with all of his characteristics and balances
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode
public class Cashier {
    private long id;
    private String name;
    private Map<Currency, Balance> balance;

    public Cashier withUpdatedCurrencyBalance(Currency currency, Balance newBalance) {
        Map<Currency, Balance> newBalances = new EnumMap<>(this.balance);
        newBalances.put(currency, newBalance);
        return new Cashier(this.id, this.name, Collections.unmodifiableMap(newBalances));
    }
}
