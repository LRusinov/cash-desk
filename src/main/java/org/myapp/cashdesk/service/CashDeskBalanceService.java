package org.myapp.cashdesk.service;

import org.myapp.cashdesk.dto.response.CashierHistoryDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface contains all cash desk balance methods.
 */
public interface CashDeskBalanceService {
    List<CashierHistoryDTO> getCashierBalanceByNameAndPeriod(String cashierName, LocalDate dateFrom, LocalDate dateTo);
}
