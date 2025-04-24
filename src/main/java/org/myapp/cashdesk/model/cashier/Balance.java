package org.myapp.cashdesk.model.cashier;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.myapp.cashdesk.model.denomination.Denomination;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
public class Balance {
    private Map<Denomination, Integer> denominations;
    private BigDecimal totalAmount;
}