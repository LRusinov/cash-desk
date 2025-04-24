package org.myapp.cashdesk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.dto.request.CashOperationRequestDTO;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DepositService extends OperationBaseService {

    private final CashierService cashierService;

    public Transaction processDeposit(final CashOperationRequestDTO request) {
        final Cashier originalCashier = cashierService.findCashier(request.cashierId());
        final Currency requestedCurrency = request.currency();
        final Balance originalBalance = getBalanceForCurrency(originalCashier, requestedCurrency);
        final Map<Denomination, Integer> updatedDenominations = new HashMap<>(originalBalance.getDenominations());

        final Map<Denomination, Integer> deposit = convertToDenominationIntegerMap(
                requestedCurrency,
                request.denominations());

        deposit.forEach((denomination, count) -> {
            Denomination existingDenomination = findDenomination(updatedDenominations.keySet(), denomination);
            updatedDenominations.merge(existingDenomination, count, Integer::sum);
        });

        final Balance updatedBalance = new Balance(
                originalBalance.getTotalAmount().add(request.amount()),
                Collections.unmodifiableMap(updatedDenominations)
        );

        return createTransaction(request,
                cashierService.save(originalCashier.withUpdatedCurrencyBalance(requestedCurrency, updatedBalance)));
    }
}
