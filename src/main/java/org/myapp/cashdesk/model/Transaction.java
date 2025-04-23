package org.myapp.cashdesk.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class Transaction {
    private long id;
    private long cashierId;
    private TransactionType type;
    private Currency currency;
    private BigDecimal amount;
    private Map<? extends Denomination, Integer> denominations;
    private LocalDateTime timestamp;
}