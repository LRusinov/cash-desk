package org.myapp.cashdesk.dto;

import org.myapp.cashdesk.model.denomination.Denomination;

import java.math.BigDecimal;
import java.util.Map;

public record BalanceDTO(BigDecimal totalAmount, Map<Denomination, Integer> denominations){}
