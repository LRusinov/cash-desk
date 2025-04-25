package org.myapp.cashdesk.repository.impl;

import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.model.transaction.Transaction;
import org.myapp.cashdesk.repository.TransactionRepository;
import org.myapp.cashdesk.repository.impl.serializer.FileSerializer;
import org.myapp.cashdesk.repository.impl.serializer.TransactionSerializer;
import org.springframework.beans.factory.annotation.Value;
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

/**
 * Responsible for transactions being saved or read to/from file
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class FileTransactionRepository implements TransactionRepository {
    private static final String TRANSACTION_ID_PREFIX = "TX_";
    @Value("${cash-desk.storage.files.transactions}")
    private String transactionsFileName;
    @Value("${cash-desk.storage.directory}")
    private String transactionFileDirectory;
    private Path transactionsFilePath;

    private final FileSerializer<Transaction> transactionSerializer = new TransactionSerializer();
    private final Map<String, Transaction> transactionsCache = new ConcurrentHashMap<>();


    @PostConstruct
    public void init() {
        createDirectoryIfMissing();
        transactionsFilePath = Path.of(transactionFileDirectory, transactionsFileName);
        log.info("Initializing transactions");
        loadTransactions();
    }

    /**
     * Saves given transaction to file.
     *
     * @param transaction transaction to be saved
     * @return the save transaction
     */
    @Override
    public synchronized Transaction save(final Transaction transaction) {
        if (isNull(transaction.getId())) {
            transaction.setId(TRANSACTION_ID_PREFIX + generateId());
        }

        try {
            Files.writeString(
                    transactionsFilePath,
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

    /**
     * Find cashiers balance by given filters.
     *
     * @param cashierName name of the cashier
     * @param fromDate date from which will be searched
     * @param toDate date to which will be searched
     * @return list of the found transactions after the applied filters(if given any) or empty list if non found
     */
    @Override
    public List<Transaction> findByCashierAndDateRange(@Nullable final String cashierName,@Nullable final LocalDate fromDate,@Nullable final LocalDate toDate) {
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
        if (!Files.exists(transactionsFilePath)) return;

        try (Stream<String> lines = Files.lines(transactionsFilePath)) {
            lines.map(transactionSerializer::parse)
                    .filter(Objects::nonNull)
                    .forEach(t -> transactionsCache.put(t.getId(), t));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load transactions", e);
        }
    }

    private void createDirectoryIfMissing() {
        Path path = Path.of(transactionFileDirectory);
        if(!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            }catch (IOException e) {
                throw new UncheckedIOException("Failed to create directory", e);
            }
        }
    }
}