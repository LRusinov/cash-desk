package org.myapp.cashdesk.dto;

import java.math.BigDecimal;
import java.util.Map;

public record BalanceDTO(BigDecimal totalAmount, Map<BigDecimal, Integer> denominations){}
