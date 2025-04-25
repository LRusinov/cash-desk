package org.myapp.cashdesk.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myapp.cashdesk.dto.request.CashOperationRequestDTO;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.denomination.Denomination;
import org.myapp.cashdesk.model.denomination.EurDenomination;
import org.myapp.cashdesk.model.transaction.OperationType;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.myapp.cashdesk.service.CashierService;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositServiceV1Test {

    @Mock
    private CashierService cashierService;

    @InjectMocks
    private DepositServiceV1 depositService;

    private static final Long CASHIER_ID = 1L;
    private static final String CASHIER_NAME = "Georgi Petrov";
    private static final Currency CURRENCY = Currency.EUR;
    private static final BigDecimal AMOUNT = new BigDecimal("100.00");
    private static final Map<BigDecimal, Integer> DENOMINATIONS = Map.of(EurDenomination.FIFTY_EUROS.getValue(), 2
    );

    @Test
    void processDeposit_shouldCreateCorrectTransaction() {
        final Map<Denomination, Integer> expectedDenominations = Map.of(EurDenomination.FIFTY_EUROS, 2);

        //GIVEN
        CashOperationRequestDTO request = createRequest();
        Cashier originalCashier = createCashierWithBalance(new BigDecimal("500.00"));
        Cashier updatedCashier = createCashierWithBalance(new BigDecimal("600.00"));

        when(cashierService.findCashier(CASHIER_ID)).thenReturn(originalCashier);
        when(cashierService.save(any())).thenReturn(updatedCashier);

        //WHEN
        Transaction result = depositService.processDeposit(request);
        //THEN
        assertNotNull(result);
        assertEquals(CASHIER_ID, result.getCashierId());
        assertEquals(CASHIER_NAME, result.getCashierName());
        assertEquals(OperationType.DEPOSIT, result.getType());
        assertEquals(CURRENCY, result.getCurrency());
        assertEquals(AMOUNT, result.getAmount());
        assertEquals(expectedDenominations, result.getDenominations());

        verify(cashierService).findCashier(CASHIER_ID);
        verify(cashierService).save(any(Cashier.class));
    }

    @Test
    void processDeposit_shouldUpdateBalanceCorrectly() {
        //GIVEN
        CashOperationRequestDTO request = createRequest();
        Balance originalBalance = new Balance(new BigDecimal("500.00"),
                Map.of(EurDenomination.FIFTY_EUROS, 10));
        Cashier originalCashier = new Cashier(CASHIER_ID, CASHIER_NAME,
                Map.of(CURRENCY, originalBalance));

        when(cashierService.findCashier(CASHIER_ID)).thenReturn(originalCashier);
        when(cashierService.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        //WHEN
        Transaction result = depositService.processDeposit(request);

        //THEN
        Balance updatedBalance = result.getNewCashierBalances().get(CURRENCY);

        assertEquals(new BigDecimal("600.00"), updatedBalance.getTotalAmount());
        assertEquals(12, updatedBalance.getDenominations().get(EurDenomination.FIFTY_EUROS));
    }

    @Test
    void processDeposit_shouldHandleNewCurrency() {
        //GIVEN
        CashOperationRequestDTO request = createRequest();
        Cashier originalCashier = new Cashier(CASHIER_ID, CASHIER_NAME,
                Map.of(Currency.EUR, new Balance(BigDecimal.valueOf(1000), Map.of(EurDenomination.FIFTY_EUROS, 20))));

        when(cashierService.findCashier(CASHIER_ID)).thenReturn(originalCashier);
        when(cashierService.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        //WHEN
        Transaction result = depositService.processDeposit(request);

        //THEN
        Balance newBalance = result.getNewCashierBalances().get(CURRENCY);

        assertNotNull(newBalance);
        assertEquals(new BigDecimal("1100.00"), newBalance.getTotalAmount());
        assertEquals(22, newBalance.getDenominations().get(EurDenomination.FIFTY_EUROS));
    }

    private CashOperationRequestDTO createRequest() {
        return new CashOperationRequestDTO(
                CASHIER_ID,
                CURRENCY,
                OperationType.DEPOSIT,
                AMOUNT,
                DENOMINATIONS
        );
    }

    private Cashier createCashierWithBalance(BigDecimal balanceAmount) {
        Balance balance = new Balance(balanceAmount,
                Map.of(EurDenomination.FIFTY_EUROS, 10));
        return new Cashier(CASHIER_ID, CASHIER_NAME, Map.of(CURRENCY, balance));
    }
}