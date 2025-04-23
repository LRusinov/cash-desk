package org.myapp.cashdesk.repository;

import org.myapp.cashdesk.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    List<Transaction> findByCashierAndDateRange(String cashierName,
                                                LocalDateTime from,
                                                LocalDateTime to);
}
