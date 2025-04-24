package org.myapp.cashdesk.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.myapp.cashdesk.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Transaction save(final Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
