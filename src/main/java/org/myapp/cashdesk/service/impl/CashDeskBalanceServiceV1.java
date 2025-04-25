package org.myapp.cashdesk.service.impl;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static java.util.Objects.nonNull;
import static org.myapp.cashdesk.utils.DenominationUtils.convertAllKeysToBigDecimal;

/**
 * Service is responsible for handling different operations with the cashier's balances
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CashDeskBalanceServiceV1 implements CashDeskBalanceService {
    private static final String EMPTY_STRING = "";

    private final TransactionService transactionService;

    /**
     * Gets cashiers balances history by optionally given cashier's name and date range.
     *
     * @param cashierName name of the cashier
     * @param dateFrom start date from which the search stars
     * @param dateTo end date to which the search ends
     * @return list of found cashier's balances history
     */
    public List<CashierHistoryDTO> getCashierBalanceByNameAndPeriod(@Nullable final String cashierName,
                                                                    @Nullable final LocalDate dateFrom,
                                                                    @Nullable final LocalDate dateTo) {
        log.info("Getting cashiers balances by given non-null filters: {} {} {}",
                nonNull(cashierName) ? "Cashier Name:" + cashierName: EMPTY_STRING,
                nonNull(dateFrom) ? "Date from:" +dateFrom : EMPTY_STRING,
                nonNull(dateTo) ? "Date to:" + dateTo: EMPTY_STRING);
        return transactionService.findByCashierNameAndDateRange(cashierName, dateFrom, dateTo).entrySet()
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

    private List<BalanceOnDateDTO> convertToBalanceOnDateDTOList(final List<Transaction> entry) {
        return entry.stream().map(this::getBalanceOnDateDto).toList();
    }

    private static String getTransactionCashierName(List<Transaction> entry) {
        if (entry.isEmpty()) {
            throw new IllegalStateException("Transaction must have cashier name!");
        }
        return entry.get(0).getCashierName();
    }
}