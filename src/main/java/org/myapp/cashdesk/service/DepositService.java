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
@Service
@Transactional
@RequiredArgsConstructor
public class DepositService extends OperationBaseService {

    private final CashierService cashierService;
    private final TransactionService transactionService;

    public Transaction processDeposit(final CashOperationRequestDTO request) {
        Cashier cashier = cashierService.findCashier(request.cashierId());
        Balance cashierBalance = getBalanceForCurrency(cashier, request.currency());
        Map<Denomination, Integer> denominations = cashierBalance.getDenominations();

        Map<Denomination, Integer> deposit = getDenominationIntegerMap(request.currency(), request.denominations());
        deposit.forEach((denomination, count) -> {
            Denomination existingDenomination = findMatchingDenomination(denominations.keySet(), denomination);
            denominations.merge(existingDenomination, count, Integer::sum);
        });

        cashierBalance.setTotalAmount(cashierBalance.getTotalAmount().add(request.amount()));
        cashierService.save(cashier);

        return transactionService.save(createTransaction(request, cashier));
    }
}
