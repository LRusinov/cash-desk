package org.myapp.cashdesk.service.impl;


import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.myapp.cashdesk.repository.TransactionRepository;
import org.myapp.cashdesk.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for managing transactions.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceV1 implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public Transaction save(final Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    /**
     * Gets cashier balances history from the transaction history by optionally given cashier's name and date range.
     *
     * @param cashierName name of the cashier
     * @param dateFrom start date from which the search stars
     * @param dateTo end date to which the search ends
     * @return map with key the unique identifier of the cashier and value list of found cashier's balances history
     */
    @Override
    public Map<Long, List<Transaction>> findByCashierNameAndDateRange(@Nullable final String cashierName, final LocalDate dateFrom,
                                                                      final LocalDate dateTo) {
        return transactionRepository.findByCashierAndDateRange(cashierName, dateFrom, dateTo).stream()
                .collect(Collectors.groupingBy(Transaction::getCashierId));
    }
}
