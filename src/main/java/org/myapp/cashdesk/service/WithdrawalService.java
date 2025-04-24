package org.myapp.cashdesk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.dto.CashOperationRequestDTO;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.model.denomination.Denomination;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class WithdrawalService extends OperationBaseService {

    private final CashierService cashierService;

    public Transaction processWithdrawal(final CashOperationRequestDTO request) {
        Cashier cashier = cashierService.findCashier(request.cashierId());
        Balance cashierBalance = cashier.getBalance().get(request.currency());
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
        cashierService.save(cashier);

        return createTransaction(request, cashier);
    }
}
