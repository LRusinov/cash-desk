package org.myapp.cashdesk.service;

import lombok.RequiredArgsConstructor;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.myapp.cashdesk.repository.CashierRepository;
import org.myapp.cashdesk.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CashBalanceService {

    private final CashierRepository cashierRepository;
    private final TransactionRepository transactionRepository;

    public Optional<Cashier> getCashierBalance(long cashierId) {
        return cashierRepository.findById(cashierId);
    }

    public List<Transaction> getCashierTransactions(final String cashierName, final LocalDateTime fromDate, final LocalDateTime toDate) {
        return transactionRepository.findByCashierAndDateRange(cashierName, fromDate, toDate);
    }

    public List<Cashier> getAllCashiers() {
        return cashierRepository.findAll();
    }
}