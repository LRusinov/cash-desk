package org.myapp.cashdesk.model.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class Transaction {
    private String id;
    private long cashierId;
    private String cashierName;
    private OperationType type;
    private Currency currency;
    private BigDecimal amount;
    private Map<Denomination, Integer> denominations;
    private Map<Currency, Balance> newCashierBalances;
    private Instant timestamp;
}