package org.myapp.cashdesk.dto;

import java.time.LocalDate;

public record CashBalanceRequestDTO(
        String cashierName,
        LocalDate dateFrom,
        LocalDate dateTo
) { }
