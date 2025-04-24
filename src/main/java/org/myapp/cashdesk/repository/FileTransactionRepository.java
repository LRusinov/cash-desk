package org.myapp.cashdesk.repository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.myapp.cashdesk.repository.serializer.FileSerializer;
import org.myapp.cashdesk.repository.serializer.TransactionSerializer;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Slf4j
@Repository
public class FileTransactionRepository implements TransactionRepository {
    private static final String TRANSACTION_ID_PREFIX = "TX_";
    private final Path transactionsFile = Path.of("transactions.txt");

    private final FileSerializer<Transaction> transactionSerializer;
    private final Map<String, Transaction> transactionsCache;

    public FileTransactionRepository() {
        this.transactionSerializer = new TransactionSerializer();
        this.transactionsCache = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void init() {
        log.info("Initializing transactions");
        loadTransactions();
    }

    @Override
    public synchronized Transaction save(final Transaction transaction) {
        if (isNull(transaction.getId())) {
            transaction.setId(TRANSACTION_ID_PREFIX + generateId());
        }

        try {
            Files.writeString(
                    transactionsFile,
                    transactionSerializer.serialize(transaction) + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            transactionsCache.put(transaction.getId(), transaction);
            return transaction;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save transaction", e);
        }
    }

    @Override
    public List<Transaction> findByCashierAndDateRange(final String cashierName, final LocalDate fromDate, final LocalDate toDate) {
        return transactionsCache.values().stream()
                .filter(t -> isNull(cashierName) || Objects.equals(t.getCashierName(), cashierName))
                .filter(t -> isNull(fromDate) || isAfterOrEqual(fromDate, t.getTimestamp()))
                .filter(t -> isNull(toDate) || isBeforeOrEqual(toDate, t.getTimestamp()))
                .toList();
    }

    private static boolean isBeforeOrEqual(final LocalDate date, final Instant timestamp) {
        LocalDate transactionDate = timestamp
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return !transactionDate.isAfter(date);
    }

    private static boolean isAfterOrEqual(final LocalDate date, final Instant timestamp) {
        LocalDate transactionDate = timestamp
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return !transactionDate.isBefore(date);
    }

    private String generateId() {
        return String.valueOf(System.currentTimeMillis());
    }

    private void loadTransactions() {
        if (!Files.exists(transactionsFile)) return;

        try (Stream<String> lines = Files.lines(transactionsFile)) {
            lines.map(transactionSerializer::parse)
                    .filter(Objects::nonNull)
                    .forEach(t -> transactionsCache.put(t.getId(), t));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load transactions", e);
        }
    }
}