package org.myapp.cashdesk.service;

import lombok.RequiredArgsConstructor;
import org.myapp.cashdesk.model.Cashier;
import org.myapp.cashdesk.model.Transaction;
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

    public List<Transaction> getCashierTransactions(long cashierId, LocalDateTime from, LocalDateTime to) {
        return transactionRepository.findByCashierAndDateRange(cashierId, from, to);
    }

    public List<Cashier> getAllCashiers() {
        return cashierRepository.findAll();
    }
}