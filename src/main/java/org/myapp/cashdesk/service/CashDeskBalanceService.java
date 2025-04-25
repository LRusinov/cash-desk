package org.myapp.cashdesk.service;

import org.myapp.cashdesk.dto.response.CashierHistoryDTO;

import java.time.LocalDate;
import java.util.List;

public interface CashDeskBalanceService {
    List<CashierHistoryDTO> getCashierBalanceByNameAndPeriod(String cashierName, LocalDate dateFrom, LocalDate dateTo);
}
