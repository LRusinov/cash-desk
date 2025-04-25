package org.myapp.cashdesk.service;

import org.myapp.cashdesk.model.transaction.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interface contain all need method for managing transactions.
 */
public interface TransactionService {
    Transaction save(Transaction transaction);

    Map<Long, List<Transaction>> findByCashierNameAndDateRange(String cashierName, LocalDate fromDate, LocalDate toDate);
}
