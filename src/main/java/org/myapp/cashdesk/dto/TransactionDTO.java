package org.myapp.cashdesk.dto;

import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;
import org.myapp.cashdesk.model.transaction.OperationType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record TransactionDTO(String id,
                             long cashierId,
                             String cashierName,
                             OperationType type,
                             Currency currency,
                             BigDecimal amount,
                             Map<Denomination, Integer> denominations,
                             Map<Currency, BalanceDTO> newCashierBalances,
                             Instant timestamp) {

}
