package org.myapp.cashdesk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.dto.CashOperationRequestDTO;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;
import org.myapp.cashdesk.model.denomination.DenominationFactory;
import org.myapp.cashdesk.model.transaction.OperationType;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.myapp.cashdesk.repository.CashierRepository;
import org.myapp.cashdesk.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashOperationService {

    private final CashierRepository cashierRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction processOperation(final CashOperationRequestDTO request) {
        validateRequest(request);

        Cashier cashier = cashierRepository.findById(request.cashierId())
                .orElseThrow(() -> new IllegalArgumentException("Cashier not found"));

        Balance currentBalance = getBalanceForCurrency(cashier, request.currency());
        if (request.operationType() == OperationType.WITHDRAWAL) {
            processWithdrawal(currentBalance, request);
        } else {
            processDeposit(currentBalance, request);
        }

        Transaction transaction = createTransaction(request, cashier);
        cashierRepository.save(cashier);
        return transactionRepository.save(transaction);
    }

    private void validateRequest(final CashOperationRequestDTO request) {
        if (request.denominations().isEmpty()) {
            throw new IllegalArgumentException("At least one denomination must be specified");
        }

        BigDecimal calculatedAmount = request.denominations().entrySet().stream()
                .map(e -> e.getKey().multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (calculatedAmount.compareTo(request.amount()) != 0) {
            throw new IllegalArgumentException("Denominations don't match the specified amount");
        }
    }

    private void processWithdrawal(final Balance cashierBalance, final CashOperationRequestDTO request) {
        Map<Denomination, Integer> cashierDenomination = cashierBalance.getDenominations();
        Map<Denomination, Integer> requestedBalance = getDenominationIntegerMap(request.currency(), request.denominations());
        requestedBalance.forEach((denomination, requestedCount) -> {
            Denomination existingDenomination = findMatchingDenomination(cashierDenomination.keySet(), denomination);
            int availableCount = cashierDenomination.getOrDefault(existingDenomination, 0);

            if (availableCount < requestedCount) {
                throw new IllegalArgumentException(
                        "Not enough " + denomination.getValue() + " " + request.currency() + " banknotes");
            }
            cashierDenomination.put(existingDenomination, availableCount - requestedCount);
        });

        cashierBalance.setTotalAmount(cashierBalance.getTotalAmount().subtract(request.amount()));
    }

    private void processDeposit(final Balance cashierBalance, final CashOperationRequestDTO request) {
        Map<Denomination, Integer> denominations = cashierBalance.getDenominations();
        Map<Denomination, Integer> deposit = getDenominationIntegerMap(request.currency(), request.denominations());
        deposit.forEach((denomination, count) -> {
            Denomination existingDenomination = findMatchingDenomination(denominations.keySet(), denomination);
            denominations.merge(existingDenomination, count, Integer::sum);
        });

        cashierBalance.setTotalAmount(cashierBalance.getTotalAmount().add(request.amount()));
    }

    private Denomination findMatchingDenomination(Iterable<Denomination> existingDenominations,
                                                  Denomination target) {
        for (Denomination d : existingDenominations) {
            if (d.getValue().compareTo(target.getValue()) == 0) {
                return d;
            }
        }
        return DenominationFactory.createDenomination(target.getCurrency(), target.getValue());
    }

    private Balance getBalanceForCurrency(final Cashier cashier, final Currency currency) {
        return cashier.getBalance().get(currency);
    }

    private Transaction createTransaction(final CashOperationRequestDTO request, Cashier cashier) {
        return new Transaction(
                null,
                cashier.getId(),
                cashier.getName(),
                request.operationType(),
                request.currency(),
                request.amount(),
                getDenominationIntegerMap(request.currency(), request.denominations()),
                LocalDateTime.now()
        );
    }

    private static Map<Denomination, Integer> getDenominationIntegerMap(Currency currency, Map<BigDecimal, Integer> denominations) {
        return denominations.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> DenominationFactory.createDenomination(currency, entry.getKey()),
                        Map.Entry::getValue
                ));
    }
}