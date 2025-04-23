package org.myapp.cashdesk.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
public class CurrencyBalance {
    private Map<Denomination, Integer> denominations;
    private BigDecimal totalAmount;
}