package org.myapp.cashdesk.model.cashier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.myapp.cashdesk.model.denomination.Denomination;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class Balance {
    private BigDecimal totalAmount;
    private Map<Denomination, Integer> denominations;
}