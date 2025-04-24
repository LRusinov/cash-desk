package org.myapp.cashdesk.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.transaction.OperationType;

import java.math.BigDecimal;
import java.util.Map;


public record CashOperationRequestDTO(
        @NotNull Long cashierId,
        @NotNull Currency currency,
        @NotNull OperationType operationType,
        @NotNull @Positive BigDecimal amount,
        @NotNull Map<@Positive BigDecimal, @PositiveOrZero Integer> denominations
) {
}
