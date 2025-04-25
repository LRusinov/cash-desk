package org.myapp.cashdesk.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myapp.cashdesk.dto.response.BalanceDTO;
import org.myapp.cashdesk.dto.response.BalanceOnDateDTO;
import org.myapp.cashdesk.dto.response.CashierHistoryDTO;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.denomination.BgnDenomination;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.EurDenomination;
import org.myapp.cashdesk.model.transaction.OperationType;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.myapp.cashdesk.service.TransactionService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashDeskBalanceServiceV1Test {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private CashDeskBalanceServiceV1 balanceService;

    private static final String CASHIER_NAME = "Asen";
    private static final LocalDate DATE_FROM = LocalDate.of(2023, 1, 1);
    private static final LocalDate DATE_TO = LocalDate.of(2023, 1, 31);
    private static final Instant TIMESTAMP = Instant.parse("2023-01-15T12:00:00Z");

    @Test
    void getCashierBalance_shouldReturnCorrectBalanceHistory() {
        //GIVEN
        Map<Currency, Balance> testBalance =
                Map.of(Currency.EUR, new Balance(BigDecimal.valueOf(500), Map.of(EurDenomination.FIVE_EUROS, 100)),
                        Currency.BGN, new Balance(BigDecimal.valueOf(1000), Map.of(BgnDenomination.TEN_LEVA, 100)));
        Transaction transaction1 = createTestTransaction(
                "txn-1234", CASHIER_NAME, Currency.BGN,testBalance);
        Transaction transaction2 = createTestTransaction(
                "txn-22345", CASHIER_NAME, Currency.EUR, testBalance);
        Map<Long, List<Transaction>> mockTransactions = Map.of(1L, List.of(transaction1, transaction2));

        //MOCK
        when(transactionService.findByCashierAndDateRange(CASHIER_NAME, DATE_FROM, DATE_TO))
                .thenReturn(mockTransactions);

        //WHEN
        List<CashierHistoryDTO> result = balanceService.getCashierBalance(CASHIER_NAME, DATE_FROM, DATE_TO);

        //THEN
        assertEquals(1, result.size());
        CashierHistoryDTO historyDTO = result.get(0);
        assertEquals(CASHIER_NAME, historyDTO.cashierName());

        List<BalanceOnDateDTO> balances = historyDTO.balanceOnDates();
        assertEquals(2, balances.size());

        BalanceOnDateDTO balanceOnDate = balances.get(0);
        assertEquals(2, balanceOnDate.balances().size());

        BalanceDTO bgnBalance = balanceOnDate.balances().get(Currency.BGN);
        assertEquals(BigDecimal.valueOf(1000), bgnBalance.totalAmount());
        assertEquals(Map.of(BigDecimal.TEN, 100), bgnBalance.denominations());

        BalanceDTO eurBalance = balanceOnDate.balances().get(Currency.EUR);
        assertEquals(BigDecimal.valueOf(500), eurBalance.totalAmount());
        assertEquals(Map.of(BigDecimal.valueOf(5), 100), eurBalance.denominations());
    }

    @Test
    void getCashierBalance_shouldHandleEmptyTransactionList() {
        //MOCKING
        when(transactionService.findByCashierAndDateRange(CASHIER_NAME, DATE_FROM, DATE_TO))
                .thenReturn(Map.of());

        //WHEN
        List<CashierHistoryDTO> result = balanceService.getCashierBalance(CASHIER_NAME, DATE_FROM, DATE_TO);

        //THEN
        assertTrue(result.isEmpty());
    }

    private Transaction createTestTransaction(String id, String cashierName, Currency currency,
                                              Map<Currency, Balance> balances) {
        return new Transaction(
                id,
                1L,
                cashierName,
                OperationType.DEPOSIT,
                currency,
                BigDecimal.valueOf(100),
                Map.of(),
                balances,
                TIMESTAMP
        );
    }
}