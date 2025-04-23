package org.myapp.cashdesk.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class Transaction {
    private String id;
    private long cashierId;
    private String cashierName;
    private OperationType type;
    private Currency currency;
    private BigDecimal amount;
    private Map<? extends Denomination, Integer> denominations;
    private LocalDateTime timestamp;
}