package org.myapp.cashdesk.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.myapp.cashdesk.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Transaction save(final Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> findByCashierAndDateRange(final String cashierName, final LocalDateTime fromDate, final LocalDateTime toDate) {
        return transactionRepository.findByCashierAndDateRange(cashierName, fromDate, toDate);
    }
}
