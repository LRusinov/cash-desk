package org.myapp.cashdesk.dto;

import org.myapp.cashdesk.model.Currency;
import org.myapp.cashdesk.model.OperationType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionDTO(Instant timestamp,
                             Currency currency,
                             OperationType type,
                             BigDecimal amount) {
}
