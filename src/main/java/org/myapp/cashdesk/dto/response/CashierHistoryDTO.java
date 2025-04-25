package org.myapp.cashdesk.dto.response;

import java.util.List;

/**
 * Contains balance history of specific cashier
 *
 * @param cashierId unique identifier of the cashier
 * @param cashierName cashier's name
 * @param balanceOnDates map with history of balances
 */
public record CashierHistoryDTO(long cashierId, String cashierName, List<BalanceOnDateDTO> balanceOnDates) {
}
