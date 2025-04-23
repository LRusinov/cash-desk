package org.myapp.cashdesk.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@Data
public class Transaction {
    private long id;
    private long cashierId;
    private TransactionType type;
    private Currency currency;
    private BigDecimal amount;
    private Map<Enum<?>, Integer> denominations;
    private LocalDateTime timestamp;
}
