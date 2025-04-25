package org.myapp.cashdesk.repository;

import org.myapp.cashdesk.model.transaction.Transaction;

import java.time.LocalDate;
import java.util.List;

/**
 * Base transaction repository interface for needed CRUD operations.
 */
public interface TransactionRepository {
    Transaction save(Transaction transaction);

    List<Transaction> findByCashierAndDateRange(String cashierName,
                                                LocalDate fromDate,
                                                LocalDate toDate);
}
