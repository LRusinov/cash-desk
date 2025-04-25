package org.myapp.cashdesk.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.myapp.cashdesk.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceV1Test {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceV1 transactionService;

    private static final String CASHIER_NAME = "Peter";
    private static final LocalDate FROM_DATE = LocalDate.of(2023, 1, 1);
    private static final LocalDate TO_DATE = LocalDate.of(2023, 1, 31);
    private static final Long CASHIER_ID_1 = 1L;
    private static final Long CASHIER_ID_2 = 2L;

    @Test
    void save_shouldDelegateToRepository() {
        //GIVEN
        Transaction transaction = getTransaction();
        Transaction savedTransaction = getTransaction();
        when(transactionRepository.save(transaction)).thenReturn(savedTransaction);

        //WHEN
        Transaction result = transactionService.save(transaction);

        //THEN
        assertSame(savedTransaction, result);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void findByCashierNameAndDateRange_shouldReturnEmptyMapWhenNoTransactions() {
        //GIVEN
        when(transactionRepository.findByCashierAndDateRange(CASHIER_NAME, FROM_DATE, TO_DATE))
                .thenReturn(List.of());

        //WHEN
        Map<Long, List<Transaction>> result = transactionService.findByCashierNameAndDateRange(CASHIER_NAME, FROM_DATE, TO_DATE);

        //THEN
        assertTrue(result.isEmpty());
        verify(transactionRepository).findByCashierAndDateRange(CASHIER_NAME, FROM_DATE, TO_DATE);
    }

    @Test
    void findByCashierAndDateRange_shouldGroupTransactionsByCashierNameId() {
        //GIVEN
        Transaction transaction1 = createTestTransaction(CASHIER_ID_1, "Peter");
        Transaction transaction2 = createTestTransaction(CASHIER_ID_1, "Peter");
        Transaction transaction3 = createTestTransaction(CASHIER_ID_2, "Peter");

        when(transactionRepository.findByCashierAndDateRange(CASHIER_NAME, FROM_DATE, TO_DATE))
                .thenReturn(List.of(transaction1, transaction2, transaction3));

        //WHEN
        Map<Long, List<Transaction>> result = transactionService.findByCashierNameAndDateRange(CASHIER_NAME, FROM_DATE, TO_DATE);

        //THEN
        assertEquals(2, result.size());
        assertEquals(2, result.get(CASHIER_ID_1).size());
        assertEquals(1, result.get(CASHIER_ID_2).size());
        assertTrue(result.get(CASHIER_ID_1).contains(transaction1));
        assertTrue(result.get(CASHIER_ID_1).contains(transaction2));
        assertTrue(result.get(CASHIER_ID_2).contains(transaction3));
    }

    @Test
    void findByCashierNameAndDateRange_shouldHandleMultipleCashiersWithSameName() {
        //GIVEN
        Transaction transaction1 = createTestTransaction(CASHIER_ID_1, CASHIER_NAME);
        Transaction transaction2 = createTestTransaction(CASHIER_ID_2, CASHIER_NAME);

        when(transactionRepository.findByCashierAndDateRange(CASHIER_NAME, FROM_DATE, TO_DATE))
                .thenReturn(List.of(transaction1, transaction2));

        //WHEN
        Map<Long, List<Transaction>> result = transactionService.findByCashierNameAndDateRange(CASHIER_NAME, FROM_DATE, TO_DATE);

        //THEN
        assertEquals(2, result.size());
        assertEquals(1, result.get(CASHIER_ID_1).size());
        assertEquals(1, result.get(CASHIER_ID_2).size());
    }

    private Transaction createTestTransaction(Long cashierId, String cashierName) {
        Transaction transaction =
                new Transaction("tx",cashierId,cashierName,null,null,null,null, null,null);
        transaction.setCashierId(cashierId);
        transaction.setCashierName(cashierName);
        return transaction;
    }

    private static Transaction getTransaction() {
        return new Transaction("tx", 1, "", null, null, null, null, null, null);
    }
}