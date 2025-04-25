package org.myapp.cashdesk.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myapp.cashdesk.exception.EntityNotFoundException;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.repository.CashierRepository;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashierServiceV1Test {

    @Mock
    private CashierRepository cashierRepository;

    @InjectMocks
    private CashierServiceV1 cashierService;

    private static final long CASHIER_ID = 1L;
    private static final String CASHIER_NAME = "Ivan Ivanov";

    @Test
    void save_shouldDelegateToRepository() {
        //GIVEN
        Cashier cashierToSave = new Cashier(CASHIER_ID, CASHIER_NAME, Map.of());
        Cashier savedCashier = new Cashier(CASHIER_ID, CASHIER_NAME, Map.of());

        when(cashierRepository.save(cashierToSave)).thenReturn(savedCashier);

        //WHEN
        Cashier result = cashierService.save(cashierToSave);

        //THEN
        assertNotNull(result);
        assertEquals(savedCashier, result);
        verify(cashierRepository).save(cashierToSave);
    }

    @Test
    void findCashier_shouldReturnCashierWhenExists() {
        //GIVEN
        Cashier expectedCashier = new Cashier(CASHIER_ID, CASHIER_NAME, Map.of());
        when(cashierRepository.findById(CASHIER_ID)).thenReturn(Optional.of(expectedCashier));

        //WHEN
        Cashier result = cashierService.findCashier(CASHIER_ID);

        //THEN
        assertNotNull(result);
        assertEquals(expectedCashier, result);
        verify(cashierRepository).findById(CASHIER_ID);
    }

    @Test
    void findCashier_shouldThrowExceptionWhenNotFound() {
        //GIVEN
        when(cashierRepository.findById(CASHIER_ID)).thenReturn(Optional.empty());

        //WHEN
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cashierService.findCashier(CASHIER_ID));

        assertEquals(String.format("Cashier with id=%d not found!", CASHIER_ID), exception.getMessage());
        verify(cashierRepository).findById(CASHIER_ID);
    }

    @Test
    void findCashier_shouldThrowExceptionWithCorrectMessage() {
        //GIVEN
        long nonExistingId = 999L;
        when(cashierRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        //WHEN
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cashierService.findCashier(nonExistingId));

        assertEquals(String.format("Cashier with id=%d not found!", nonExistingId), exception.getMessage());
    }
}