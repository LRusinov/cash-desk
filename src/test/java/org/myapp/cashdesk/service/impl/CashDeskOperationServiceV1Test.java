package org.myapp.cashdesk.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myapp.cashdesk.dto.request.CashOperationRequestDTO;
import org.myapp.cashdesk.dto.response.TransactionDTO;
import org.myapp.cashdesk.model.denomination.BgnDenomination;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;
import org.myapp.cashdesk.model.transaction.OperationType;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.myapp.cashdesk.service.DepositService;
import org.myapp.cashdesk.service.TransactionService;
import org.myapp.cashdesk.service.WithdrawalService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashDeskOperationServiceV1Test {

    @Mock
    private WithdrawalService withdrawalService;

    @Mock
    private DepositService depositService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private CashDeskOperationServiceV1 operationService;

    private static final Long CASHIER_ID = 1L;
    private static final String CASHIER_NAME = "Ivaylo";
    private static final Currency CURRENCY = Currency.BGN;
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(100);
    private static final Map<Denomination, Integer> DENOMINATIONS = Map.of(BgnDenomination.TEN_LEVA, 10);
    private static final Map<BigDecimal, Integer> BIG_DECIMAL_DENOMINATIONS = Map.of(BigDecimal.TEN, 10);
    private static final Instant TIMESTAMP = Instant.now();

    @Test
    void processOperation_shouldProcessDeposit() {
        //GIVEN
        CashOperationRequestDTO request = createRequest(OperationType.DEPOSIT);
        Transaction expectedTransaction = createTransaction(OperationType.DEPOSIT);

        //MOCKING
        when(depositService.processDeposit(request)).thenReturn(expectedTransaction);
        when(transactionService.save(expectedTransaction)).thenReturn(expectedTransaction);

        //WHEN
        TransactionDTO result = operationService.processOperation(request);

        //THEN
        assertNotNull(result);
        assertEquals(expectedTransaction.getId(), result.id());
        verify(depositService).processDeposit(request);
        verify(transactionService).save(expectedTransaction);
    }

    @Test
    void processOperation_shouldProcessWithdrawal() {
        //GIVEN
        CashOperationRequestDTO request = createRequest(OperationType.WITHDRAWAL);
        Transaction expectedTransaction = createTransaction(OperationType.WITHDRAWAL);

        when(withdrawalService.processWithdrawal(request)).thenReturn(expectedTransaction);
        when(transactionService.save(expectedTransaction)).thenReturn(expectedTransaction);

        //WHEN
        TransactionDTO result = operationService.processOperation(request);

        //THEN
        assertNotNull(result);
        assertEquals(expectedTransaction.getId(), result.id());
        verify(withdrawalService).processWithdrawal(request);
        verify(transactionService).save(expectedTransaction);
    }


    private CashOperationRequestDTO createRequest(OperationType operationType) {
        return new CashOperationRequestDTO(
                CASHIER_ID,
                CURRENCY.name(),
                operationType.name(),
                AMOUNT,
                BIG_DECIMAL_DENOMINATIONS
        );
    }

    private Transaction createTransaction(OperationType operationType) {
        return new Transaction(
                "txn-123",
                CASHIER_ID,
                CASHIER_NAME,
                operationType,
                CURRENCY,
                AMOUNT,
                DENOMINATIONS,
                Map.of(),
                TIMESTAMP
        );
    }
}