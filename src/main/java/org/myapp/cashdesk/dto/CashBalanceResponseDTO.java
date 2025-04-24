package org.myapp.cashdesk.dto;

import org.myapp.cashdesk.model.cashier.Balance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record CashBalanceResponseDTO(LocalDateTime onDate, Map<BigDecimal, Balance> balances) {
}
