package org.myapp.cashdesk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.dto.BalanceDTO;
import org.myapp.cashdesk.dto.CashOperationRequestDTO;
import org.myapp.cashdesk.dto.TransactionDTO;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static org.myapp.cashdesk.utils.DenominationUtils.convertAllKeysToBigDecimal;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CashDeskOperationService {

    private final WithdrawalService withdrawalService;
    private final DepositService depositService;
    private final TransactionService transactionService;

    public TransactionDTO processOperation(final CashOperationRequestDTO request) {
        validateRequest(request);

        Transaction transaction =
                switch (request.operationType()) {
                    case WITHDRAWAL -> withdrawalService.processWithdrawal(request);
                    case DEPOSIT -> depositService.processDeposit(request);
                };

        return convertToDto(transactionService.save(transaction));
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

    private TransactionDTO convertToDto(final Transaction transaction) {

        return new TransactionDTO(transaction.getId(),
                transaction.getCashierId(),
                transaction.getCashierName(),
                transaction.getType(),
                transaction.getCurrency(),
                transaction.getAmount(),
                Collections.unmodifiableMap(transaction.getDenominations()),
                convertToDto(transaction.getNewCashierBalances()),
                transaction.getTimestamp());
    }

    private Map<Currency, BalanceDTO> convertToDto(Map<Currency, Balance> balanceMap) {
        return Collections.unmodifiableMap(
                balanceMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> convertBalanceToDTO(entry.getValue())
                        )));
    }

    private BalanceDTO convertBalanceToDTO(Balance balance) {
        return new BalanceDTO(balance.getTotalAmount(), convertAllKeysToBigDecimal(balance.getDenominations()));
    }
}