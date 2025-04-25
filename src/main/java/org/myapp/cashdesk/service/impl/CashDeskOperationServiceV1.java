package org.myapp.cashdesk.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.dto.response.BalanceDTO;
import org.myapp.cashdesk.dto.request.CashOperationRequestDTO;
import org.myapp.cashdesk.dto.response.TransactionDTO;
import org.myapp.cashdesk.exception.CashDeskValidationException;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.myapp.cashdesk.service.CashDeskOperationService;
import org.myapp.cashdesk.service.DepositService;
import org.myapp.cashdesk.service.TransactionService;
import org.myapp.cashdesk.service.WithdrawalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static org.myapp.cashdesk.utils.DenominationUtils.convertAllKeysToBigDecimal;

/**
 * Service is responsible for handling all Cash Desk operations
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CashDeskOperationServiceV1 implements CashDeskOperationService {

    private final WithdrawalService withdrawalService;
    private final DepositService depositService;
    private final TransactionService transactionService;

    /**
     * Processes the cash desk operation
     *
     * @param request requested operation and all need data to process it
     * @return object containing all the information of the executed transaction
     */
    public TransactionDTO processOperation(final CashOperationRequestDTO request) {
        validateRequestDto(request);

        Transaction transaction =
                switch (request.operationType()) {
                    case WITHDRAWAL -> withdrawalService.processWithdrawal(request);
                    case DEPOSIT -> depositService.processDeposit(request);
                };

        return convertToDto(transactionService.save(transaction));
    }

    private void validateRequestDto(final CashOperationRequestDTO request) {
        log.info("Validating request");
        if (request.denominations().isEmpty()) {
            throw new CashDeskValidationException("At least one denomination must be specified!");
        }

        BigDecimal calculatedAmount = request.denominations().entrySet().stream()
                .map(e -> e.getKey().multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (calculatedAmount.compareTo(request.amount()) != 0) {
            throw new CashDeskValidationException("Denominations don't match the specified amount!");
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

    private Map<Currency, BalanceDTO> convertToDto(final Map<Currency, Balance> balanceMap) {
        return Collections.unmodifiableMap(
                balanceMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> convertBalanceToDTO(entry.getValue())
                        )));
    }

    private BalanceDTO convertBalanceToDTO(final Balance balance) {
        return new BalanceDTO(balance.getTotalAmount(), convertAllKeysToBigDecimal(balance.getDenominations()));
    }
}