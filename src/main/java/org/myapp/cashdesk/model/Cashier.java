package org.myapp.cashdesk.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Cashier {
    private long id;
    private String name;
    private CurrencyBalance bgnBalance;
    private CurrencyBalance eurBalance;
}
