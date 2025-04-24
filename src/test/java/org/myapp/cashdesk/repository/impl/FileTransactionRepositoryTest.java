package org.myapp.cashdesk.repository.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.denomination.BgnDenomination;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.transaction.OperationType;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
class FileTransactionRepositoryTest {
    private static final  Map<Currency, Balance> TEST_BALANCE = Map.of(Currency.EUR, new Balance(BigDecimal.valueOf(1000), Map.of(BgnDenomination.FIFTY_LEVA, 20)));
    private static final String TEST_CASHIER_NAME = "TEST_CASHIER_NAME";
    private static final Instant NOW = Instant.now();
    public static final String TEST_TRANSACTION_ID = "TEST_ID";

    @Value("${cash-desk.storage.files.transactions}")
    private String transactionsFile;

    @Value("${cash-desk.storage.directory}")
    private String storageDir;

    @Autowired
    private FileTransactionRepository repository;

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Path.of(storageDir, transactionsFile));
    }

    @Test
    void save_shouldGenerateIdForNewTransaction() {
        Transaction tx = createTestTransaction(null, TEST_CASHIER_NAME, NOW);
        Transaction saved = repository.save(tx);

        assertNotNull(saved.getId());
        assertTrue(saved.getId().startsWith("TX_"));
        assertEquals(TEST_CASHIER_NAME, saved.getCashierName());
    }

    @Test
    void save_shouldPersistToFile() {
        Transaction testTransaction = createTestTransaction(null, TEST_CASHIER_NAME, NOW);
        repository.save(testTransaction);

        assertTrue(Files.exists(Path.of(storageDir, transactionsFile)));
    }

    @Test
    void findByCashierAndDateRange_shouldHandleNullInputs() {
        repository.save(createTestTransaction(TEST_TRANSACTION_ID, TEST_CASHIER_NAME, NOW));

        assertDoesNotThrow(() -> {
            repository.findByCashierAndDateRange(null, null, null);
            repository.findByCashierAndDateRange("CASHIER1", null, null);
            repository.findByCashierAndDateRange(null, LocalDate.now(), null);
        });
    }

    @Test
    void save_shouldHandleExistingId() {
        Transaction tx = createTestTransaction(TEST_TRANSACTION_ID, TEST_CASHIER_NAME, NOW);
        Transaction saved = repository.save(tx);
        assertEquals(TEST_TRANSACTION_ID, saved.getId());
    }

    private Transaction createTestTransaction(String id, String cashierName, Instant timestamp) {
        return new Transaction(
                id,
                1,
                cashierName,
                OperationType.DEPOSIT,
                Currency.EUR,
                BigDecimal.valueOf(100),
                Map.of(),
                TEST_BALANCE,
                timestamp
        );
    }

}