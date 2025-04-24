package org.myapp.cashdesk.service.impl;


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

    @Override
    public Map<Long, List<Transaction>> findByCashierAndDateRange(final String cashierName, final LocalDate fromDate,
                                                                  final LocalDate toDate) {
        return transactionRepository.findByCashierAndDateRange(cashierName, fromDate, toDate).stream()
                .collect(Collectors.groupingBy(Transaction::getCashierId));
    }
}
