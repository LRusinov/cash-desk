package org.myapp.cashdesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.myapp.cashdesk.model.Currency;
import org.myapp.cashdesk.model.OperationType;

import java.math.BigDecimal;
import java.util.Map;


public record CashOperationRequestDTO(
        @NotBlank Long cashierId,
        @NotNull Currency currency,
        @NotNull OperationType operationType,
        @NotNull @Positive BigDecimal amount,
        @NotNull Map<@Positive BigDecimal, @PositiveOrZero Integer> denominations
) {
}
