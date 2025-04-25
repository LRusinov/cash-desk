package org.myapp.cashdesk.dto.response;

import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;
import org.myapp.cashdesk.model.transaction.OperationType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * Contains transactions details
 *
 * @param id unique identifier of the transaction
 * @param cashierId unique identifier of the cashier who processed the transaction
 * @param cashierName name of the cashier who processed the transaction
 * @param type type of the operation which was executed during the transaction
 * @param currency currency of the transaction
 * @param amount total amount which was processed during the transaction
 * @param denominations denomination of the amount which was processed during the transaction
 * @param newCashierBalances cashier's balance after the processed operation
 * @param timestamp time of the transaction execution
 */
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
