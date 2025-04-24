package org.myapp.cashdesk.dto;

import org.myapp.cashdesk.model.denomination.Currency;

import java.time.LocalDate;
import java.util.Map;

public record BalanceOnDateDTO (LocalDate onDate, Map<Currency, BalanceDTO> balances){}
