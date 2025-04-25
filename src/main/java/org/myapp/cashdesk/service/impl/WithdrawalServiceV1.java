package org.myapp.cashdesk.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.dto.request.CashOperationRequestDTO;
import org.myapp.cashdesk.exception.InsufficientFundsException;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.myapp.cashdesk.service.CashierService;
import org.myapp.cashdesk.service.WithdrawalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Service is responsible for handling WITHDRAWAL operations.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class WithdrawalServiceV1 extends OperationBaseService implements WithdrawalService{

    private final CashierService cashierService;

    /**
     * Processes given withdrawal operation.
     *
     * @param request contains all data needed for the processing of the withdrawal operation
     * @return object which contains the transaction information for the executed withdrawal operation
     */
    public Transaction processWithdrawal(final CashOperationRequestDTO request) {
        log.info("processWithdrawal");
        Cashier originalCashier = cashierService.findCashier(request.cashierId());
        Currency requestedCurrency = request.currency();

        Balance originalBalance = originalCashier.getBalance().get(requestedCurrency);
        Map<Denomination, Integer> updatedDenominations = calculateNewDenominations(
                originalBalance.getDenominations(),
                convertToDenominationIntegerMap(requestedCurrency, request.denominations()),
                requestedCurrency
        );

        Balance updatedBalance = new Balance(
                originalBalance.getTotalAmount().subtract(request.amount()),
                updatedDenominations
        );

        return createTransaction(request,
                cashierService.save(originalCashier.withUpdatedCurrencyBalance(requestedCurrency, updatedBalance)));
    }

    private Map<Denomination, Integer> calculateNewDenominations(final Map<Denomination, Integer> cashierDenominations,
                                                                 final Map<Denomination, Integer> requestedDenominations, Currency currency) {
        Map<Denomination, Integer> newBalance = new HashMap<>();

        requestedDenominations.forEach((requestedDenomination, requestedCount) -> {
            Denomination matchingDenomination = findDenomination(cashierDenominations.keySet(), requestedDenomination);
            int availableCount = cashierDenominations.getOrDefault(matchingDenomination, 0);

            validateDenominationAvailability(currency, requestedDenomination, requestedCount, availableCount);
            newBalance.put(matchingDenomination, availableCount - requestedCount);
        });

        cashierDenominations.forEach((denomination, count) -> {
            if (!requestedDenominations.containsKey(denomination)) {
                newBalance.put(denomination, count);
            }
        });

        return Collections.unmodifiableMap(newBalance);
    }

    private static void validateDenominationAvailability(final Currency currency, final Denomination requestedDenomination, final int requestedCount, final int availableCount) {
        if (availableCount < requestedCount) {
            throw new InsufficientFundsException(
                    "Not enough " + requestedDenomination.getValue() + " " + currency + " banknotes");
        }
    }
}
