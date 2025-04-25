package org.myapp.cashdesk.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myapp.cashdesk.dto.request.CashOperationRequestDTO;
import org.myapp.cashdesk.exception.InsufficientFundsException;
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
import static org.myapp.cashdesk.model.transaction.OperationType.WITHDRAWAL;

@ExtendWith(MockitoExtension.class)
class WithdrawalServiceV1Test {

    @Mock
    private CashierService cashierService;

    @InjectMocks
    private WithdrawalServiceV1 withdrawalService;

    private static final Long CASHIER_ID = 1L;
    private static final String CASHIER_NAME = "Ivana";
    private static final Currency CURRENCY = Currency.EUR;
    private static final BigDecimal AMOUNT = new BigDecimal("100.00");
    private static final Map<Denomination, Integer> DENOMINATIONS = Map.of(
            EurDenomination.FIFTY_EUROS, 2);
    private static final Map<BigDecimal, Integer> BIG_DECIMAL_DENOMINATIONS = Map.of(
            EurDenomination.FIFTY_EUROS.getValue(), 2
    );

    @Test
    void processWithdrawal_shouldCreateCorrectTransaction() {
        //GIVEN
        CashOperationRequestDTO request = createRequest();
        Cashier originalCashier = createCashierWithBalance(new BigDecimal("500.00"));
        Cashier updatedCashier = createCashierWithBalance(new BigDecimal("400.00"));

        when(cashierService.findCashier(CASHIER_ID)).thenReturn(originalCashier);
        when(cashierService.save(any())).thenReturn(updatedCashier);

        //WHEN
        Transaction result = withdrawalService.processWithdrawal(request);

        //THEN
        assertNotNull(result);
        assertEquals(CASHIER_ID, result.getCashierId());
        assertEquals(CASHIER_NAME, result.getCashierName());
        assertEquals(WITHDRAWAL, result.getType());
        assertEquals(CURRENCY, result.getCurrency());
        assertEquals(AMOUNT, result.getAmount());
        assertEquals(DENOMINATIONS, result.getDenominations());

        verify(cashierService).findCashier(CASHIER_ID);
        verify(cashierService).save(any(Cashier.class));
    }

    @Test
    void processWithdrawal_shouldUpdateBalanceCorrectly() {
        //GIVEN
        CashOperationRequestDTO request = createRequest();
        Balance originalBalance = new Balance(new BigDecimal("500.00"),
                Map.of(EurDenomination.FIFTY_EUROS, 10));
        Cashier originalCashier = new Cashier(CASHIER_ID, CASHIER_NAME,
                Map.of(CURRENCY, originalBalance));

        when(cashierService.findCashier(CASHIER_ID)).thenReturn(originalCashier);
        when(cashierService.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        //WHEN
        Transaction result = withdrawalService.processWithdrawal(request);

        //THEN
        Map<Currency, Balance> updatedBalance = result.getNewCashierBalances();

        assertEquals(new BigDecimal("400.00"), updatedBalance.get(CURRENCY).getTotalAmount());
        assertEquals(8, updatedBalance.get(CURRENCY).getDenominations().get(EurDenomination.FIFTY_EUROS));
    }

    @Test
    void processWithdrawal_shouldThrowWhenInsufficientDenominations() {
        //GIVEN
        CashOperationRequestDTO request = createRequest();
        Balance originalBalance = new Balance(new BigDecimal("500.00"),
                Map.of(EurDenomination.FIFTY_EUROS, 1)); // Only 1 available, but requesting 2
        Cashier originalCashier = new Cashier(CASHIER_ID, CASHIER_NAME,
                Map.of(CURRENCY, originalBalance));

        when(cashierService.findCashier(CASHIER_ID)).thenReturn(originalCashier);

        //WHEN
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
                () -> withdrawalService.processWithdrawal(request));

        assertEquals("Not enough 50 EUR banknotes", exception.getMessage());
    }

    @Test
    void processWithdrawal_shouldThrowWhenInsufficientTotalAmount() {
        //GIVEN
        CashOperationRequestDTO request = createRequest();
        Balance originalBalance = new Balance(new BigDecimal("50.00"),
                Map.of(EurDenomination.FIFTY_EUROS, 1)); // Total less than requested
        Cashier originalCashier = new Cashier(CASHIER_ID, CASHIER_NAME,
                Map.of(CURRENCY, originalBalance));

        when(cashierService.findCashier(CASHIER_ID)).thenReturn(originalCashier);

        //WHEN
        assertThrows(InsufficientFundsException.class, () -> withdrawalService.processWithdrawal(request));
    }

    @Test
    void processWithdrawal_shouldMaintainUnaffectedDenominations() {
        //GIVEN
        CashOperationRequestDTO request = createRequest();
        Map<Denomination, Integer> originalDenominations = Map.of(
                EurDenomination.FIFTY_EUROS, 10,
                EurDenomination.TWENTY_EUROS, 5
        );
        Balance originalBalance = new Balance(new BigDecimal("600.00"), originalDenominations);
        Cashier originalCashier = new Cashier(CASHIER_ID, CASHIER_NAME,
                Map.of(CURRENCY, originalBalance));

        when(cashierService.findCashier(CASHIER_ID)).thenReturn(originalCashier);
        when(cashierService.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        //WHEN
        Transaction result = withdrawalService.processWithdrawal(request);

        //THEN
        Map<Currency, Balance> updatedBalance = result.getNewCashierBalances();
        assertEquals(5, updatedBalance.get(CURRENCY).getDenominations().get(EurDenomination.TWENTY_EUROS));
    }

    private CashOperationRequestDTO createRequest() {
        return new CashOperationRequestDTO(
                CASHIER_ID,
                CURRENCY.name(),
                WITHDRAWAL.name(),
                AMOUNT,
                BIG_DECIMAL_DENOMINATIONS
        );
    }

    private Cashier createCashierWithBalance(BigDecimal balanceAmount) {
        Balance balance = new Balance(balanceAmount,
                Map.of(EurDenomination.FIFTY_EUROS, 10));
        return new Cashier(CASHIER_ID, CASHIER_NAME,
                Map.of(CURRENCY, balance));
    }
}