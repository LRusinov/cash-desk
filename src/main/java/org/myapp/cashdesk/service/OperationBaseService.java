package org.myapp.cashdesk.service;

import org.myapp.cashdesk.dto.CashOperationRequestDTO;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;
import org.myapp.cashdesk.model.denomination.DenominationFactory;
import org.myapp.cashdesk.model.transaction.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class OperationBaseService {

    protected Denomination findMatchingDenomination(Iterable<Denomination> existingDenominations,
                                                  Denomination target) {
        for (Denomination d : existingDenominations) {
            if (d.getValue().compareTo(target.getValue()) == 0) {
                return d;
            }
        }
        return DenominationFactory.createDenomination(target.getCurrency(), target.getValue());
    }

    protected Balance getBalanceForCurrency(final Cashier cashier, final Currency currency) {
        return cashier.getBalance().get(currency);
    }

    protected Transaction createTransaction(final CashOperationRequestDTO request, Cashier cashier) {
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

    protected static Map<Denomination, Integer> getDenominationIntegerMap(Currency currency, Map<BigDecimal, Integer> denominations) {
        return denominations.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> DenominationFactory.createDenomination(currency, entry.getKey()),
                        Map.Entry::getValue
                ));
    }
}
