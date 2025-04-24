package org.myapp.cashdesk.model.cashier;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.myapp.cashdesk.model.denomination.Currency;

import java.util.Map;

@AllArgsConstructor
@Data
public class Cashier {
    private long id;
    private String name;
    private Map<Currency, Balance> balance;
}
