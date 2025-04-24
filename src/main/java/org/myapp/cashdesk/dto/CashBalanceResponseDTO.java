package org.myapp.cashdesk.dto;

import org.myapp.cashdesk.model.denomination.Denomination;

import java.util.List;
import java.util.Map;

public record CashBalanceResponseDTO(String cashierName,
                                     Map<Denomination, Integer>  balances,
                                     List<TransactionDTO> transactions) {
}
