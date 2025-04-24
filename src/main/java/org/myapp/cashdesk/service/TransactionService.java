package org.myapp.cashdesk.service;

import org.myapp.cashdesk.model.transaction.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TransactionService {
    Transaction save(Transaction transaction);

    Map<Long, List<Transaction>> findByCashierAndDateRange(String cashierName, LocalDate fromDate, LocalDate toDate);
}
