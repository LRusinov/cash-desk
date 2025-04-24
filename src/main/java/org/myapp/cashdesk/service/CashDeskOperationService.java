package org.myapp.cashdesk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.dto.CashOperationRequestDTO;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CashDeskOperationService {

    private final WithdrawalService withdrawalService;
    private final DepositService depositService;
    private final TransactionService transactionService;

    public Transaction processOperation(final CashOperationRequestDTO request) {
        validateRequest(request);

        Transaction transaction =
                switch (request.operationType()) {
                    case WITHDRAWAL -> withdrawalService.processWithdrawal(request);
                    case DEPOSIT -> depositService.processDeposit(request);
                };

        return transactionService.save(transaction);
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
}