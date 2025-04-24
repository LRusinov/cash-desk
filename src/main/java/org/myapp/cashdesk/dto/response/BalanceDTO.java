package org.myapp.cashdesk.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record BalanceDTO(BigDecimal totalAmount, Map<BigDecimal, Integer> denominations){}
