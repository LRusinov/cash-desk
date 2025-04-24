package org.myapp.cashdesk.repository;

import org.myapp.cashdesk.model.transaction.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    List<Transaction> findByCashierAndDateRange(long cashierId,
                                                LocalDateTime from,
                                                LocalDateTime to);
}
