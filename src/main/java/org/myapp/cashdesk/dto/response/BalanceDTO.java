package org.myapp.cashdesk.dto.response;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Contains balance information
 *
 * @param totalAmount total amount of the balance
 * @param denominations denomination of the balance
 */
public record BalanceDTO(BigDecimal totalAmount, Map<BigDecimal, Integer> denominations){}
