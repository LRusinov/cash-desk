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
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Slf4j
@Repository
public final class FileTransactionRepository implements TransactionRepository {
    private static final String TRANSACTION_ID_PREFIX = "TX_";
    private final FileSerializer<Transaction> transactionSerializer = new TransactionSerializer();
    private final Path transactionsFile = Path.of("transactions.txt");
    private final Map<String, Transaction> transactionsCache = new ConcurrentHashMap<>();

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
    public List<Transaction> findByCashierAndDateRange(final String cashierName,final LocalDateTime fromDate, final LocalDateTime toDate) {
        return transactionsCache.values().stream()
                .filter(t -> Objects.equals(t.getCashierName(), cashierName))
                .filter(t -> fromDate == null || !t.getTimestamp().isBefore(fromDate))
                .filter(t -> toDate == null || !t.getTimestamp().isAfter(toDate))
                .collect(Collectors.toList());
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