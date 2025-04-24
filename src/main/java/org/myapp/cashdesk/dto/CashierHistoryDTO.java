package org.myapp.cashdesk.dto;

import java.util.List;

public record CashierHistoryDTO(long cashierId, String cashierName, List<BalanceOnDateDTO> balanceOnDates) {
}
