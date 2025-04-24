package org.myapp.cashdesk.service;

import lombok.AllArgsConstructor;
import org.myapp.cashdesk.dto.CashOperationRequestDTO;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;
import org.myapp.cashdesk.model.transaction.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import static org.myapp.cashdesk.utils.DenominationUtils.getDenomination;

@AllArgsConstructor
public abstract class OperationBaseService {

    protected Denomination findDenomination(final Iterable<Denomination> existingDenominations,
                                            final Denomination target) {
        for (Denomination d : existingDenominations) {
            if (d.getValue().compareTo(target.getValue()) == 0) {
                return d;
            }
        }
        return getDenomination(target.getCurrency(), target.getValue());
    }

    protected Balance getBalanceForCurrency(final Cashier cashier, final Currency currency) {
        return cashier.getBalance().get(currency);
    }

    protected Transaction createTransaction(final CashOperationRequestDTO request, final  Cashier cashier) {
        return new Transaction(
                null,
                cashier.getId(),
                cashier.getName(),
                request.operationType(),
                request.currency(),
                request.amount(),
                convertToDenominationIntegerMap(request.currency(), request.denominations()),
                cashier.getBalance(),
                Instant.now()
        );
    }

    protected static Map<Denomination, Integer> convertToDenominationIntegerMap(final Currency currency,final  Map<BigDecimal, Integer> denominations) {
        return denominations.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> getDenomination(currency, entry.getKey()),
                        Map.Entry::getValue
                ));
    }
}
