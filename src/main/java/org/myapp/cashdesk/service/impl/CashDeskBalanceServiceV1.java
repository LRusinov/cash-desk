package org.myapp.cashdesk.service.impl;

import lombok.RequiredArgsConstructor;
import org.myapp.cashdesk.dto.response.BalanceDTO;
import org.myapp.cashdesk.dto.response.BalanceOnDateDTO;
import org.myapp.cashdesk.dto.response.CashierHistoryDTO;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.myapp.cashdesk.service.CashDeskBalanceService;
import org.myapp.cashdesk.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.myapp.cashdesk.utils.DenominationUtils.convertAllKeysToBigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class CashDeskBalanceServiceV1 implements CashDeskBalanceService {

    private final TransactionService transactionService;

    public List<CashierHistoryDTO> getCashierBalance(String cashierName, LocalDate dateFrom, LocalDate dateTo) {
        return transactionService.findByCashierAndDateRange(cashierName, dateFrom, dateTo).entrySet()
                .stream()
                .map(entry -> new CashierHistoryDTO(entry.getKey(),
                        getTransactionCashierName(entry.getValue()),
                        convertToBalanceOnDateDTOList(entry.getValue())))
                .toList();
    }

    private BalanceOnDateDTO getBalanceOnDateDto(final Transaction transaction) {
        Map<Currency, BalanceDTO> balanceDTOMap =
                transaction.getNewCashierBalances().entrySet().stream()
                        .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> convertToDto(e.getValue())));
        return new BalanceOnDateDTO(
                transaction.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDate(),
                balanceDTOMap);
    }

    private BalanceDTO convertToDto(final Balance balance) {
        return new BalanceDTO(balance.getTotalAmount(), convertAllKeysToBigDecimal(balance.getDenominations()));
    }

    private List<BalanceOnDateDTO> convertToBalanceOnDateDTOList(List<Transaction> entry) {
        return entry.stream().map(this::getBalanceOnDateDto).toList();
    }

    private static String getTransactionCashierName(List<Transaction> entry) {
        if (entry.isEmpty()) {
            throw new IllegalStateException("Transaction must have cashier name!");
        }
        return entry.get(0).getCashierName();
    }
}