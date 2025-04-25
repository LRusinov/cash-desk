package org.myapp.cashdesk.repository.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.model.denomination.BgnDenomination;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.repository.CashierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class FileCashierRepositoryTest {
    private static final  Map<Currency, Balance> TEST_BALANCE = Map.of(Currency.EUR, new Balance(BigDecimal.valueOf(1000), Map.of(BgnDenomination.FIFTY_LEVA, 20)));

    @Value("${cash-desk.storage.files.cashiers}")
    private String cashiersFile;

    @Value("${cash-desk.storage.directory}")
    private String storageDir;

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Path.of(storageDir, cashiersFile));
    }

    @Autowired
    private CashierRepository repository;

    @Test
    void findById_shouldReturnEmptyOptionalForNonExistingCashier() {
        Optional<Cashier> result = repository.findById(1000L);
        assertFalse(result.isPresent());
    }

    @Test
    void save_shouldReturnCashierWithGeneratedId() {
        Cashier newCashier = new Cashier(0, "TEST-Cashier", TEST_BALANCE);
        Cashier saved = repository.save(newCashier);
        assertNotEquals(0, saved.getId());
    }

    @ParameterizedTest
    @MethodSource
    void save_shouldPersistDifferentCashiers(Cashier cashier, String expectedName) {
        Cashier saved = repository.save(cashier);
        assertEquals(expectedName, saved.getName());
        Cashier actualCashier = repository.findById(saved.getId()).orElse(null);
        assertNotNull(actualCashier);
        assertEquals(expectedName, actualCashier.getName());
    }

    private static Stream<Arguments> save_shouldPersistDifferentCashiers() {
        return Stream.of(
                Arguments.of(new Cashier(0, "Ivan", TEST_BALANCE), "Ivan"),
                Arguments.of(new Cashier(0, "Georgi", TEST_BALANCE), "Georgi"),
                Arguments.of(new Cashier(5, "Gergana", TEST_BALANCE), "Gergana")
        );
    }

    @ParameterizedTest
    @MethodSource
    void findById_shouldHandleVariousIds(long id, boolean shouldExist) {
        if (shouldExist) {
            repository.save(new Cashier(id, "Test", TEST_BALANCE));
        }
        assertEquals(shouldExist, repository.findById(id).isPresent());
    }

    private static Stream<Arguments> findById_shouldHandleVariousIds() {
        return Stream.of(
                Arguments.of(1L, true),
                Arguments.of(Long.MAX_VALUE, false),
                Arguments.of(-1L, false)
        );
    }
}