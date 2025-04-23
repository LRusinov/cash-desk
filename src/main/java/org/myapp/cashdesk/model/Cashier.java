package org.myapp.cashdesk.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class Cashier{
    private long id;
    private String name;
    private CurrencyBalance bgnBalance;
    private CurrencyBalance eurBalance;
    private BigDecimal balance;
}
