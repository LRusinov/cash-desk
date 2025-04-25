package org.myapp.cashdesk.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Request DTO containing all information needed for processing operation
 *
 * @param cashierId unique identifier  of the cashier
 * @param currency currency of the operation
 * @param operationType type of the operation
 * @param amount amount which will be processed during theoperation
 * @param denominations denomination of the amount
 */
public record CashOperationRequestDTO(
        @NotNull(message = "Cashier ID cannot be null")
        @Positive(message = "Cashier ID must be positive number")
        Long cashierId,

        @NotBlank(message = "Currency cannot be null or empty")
        String currency,

        @NotBlank(message = "Operation type cannot be null or empty")
        String operationType,

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotNull
        Map<@Positive BigDecimal, @PositiveOrZero Integer> denominations
) {
}
