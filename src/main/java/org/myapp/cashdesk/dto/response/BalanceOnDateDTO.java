package org.myapp.cashdesk.dto.response;

import org.myapp.cashdesk.model.denomination.Currency;

import java.time.LocalDate;
import java.util.Map;

/**
 * Contains balances for specific date
 *
 * @param onDate the date on which the balances were present
 * @param balances map of the balances
 */
public record BalanceOnDateDTO (LocalDate onDate, Map<Currency, BalanceDTO> balances){}
