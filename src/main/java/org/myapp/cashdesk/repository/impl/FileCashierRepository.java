package org.myapp.cashdesk.repository.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.model.denomination.Currency;
import org.myapp.cashdesk.model.cashier.Balance;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.repository.CashierRepository;
import org.myapp.cashdesk.repository.serializer.CashierSerializer;
import org.myapp.cashdesk.repository.serializer.FileSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static org.myapp.cashdesk.model.denomination.BgnDenomination.FIFTY_LEVA;
import static org.myapp.cashdesk.model.denomination.BgnDenomination.TEN_LEVA;
import static org.myapp.cashdesk.model.denomination.EurDenomination.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FileCashierRepository implements CashierRepository {

    private static final long INITIAL_ID = 1L;

    @Value("${cash-desk.storage.files.cashiers}")
    private String cashiersFileName;
    @Value("${cash-desk.storage.directory}")
    private String cashierFileDirectory;

    private final FileSerializer<Cashier> cashierSerializer = new CashierSerializer();
    private final Map<Long, Cashier> cashiersMap = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(INITIAL_ID);
    private Path cashiersFilePath;

    @PostConstruct
    public void init() {
        cashiersFilePath = Path.of(cashierFileDirectory, cashiersFileName);
        try {
            log.info("Initialized cashiers");
            createDirectoryIfMissing();
            loadCashiers();
            initializeDefaultCashiersIfEmpty();
        } catch (Exception e) {
            log.error("Initialization failed", e);
            throw new UncheckedIOException(new IOException("Repository initialization failed", e));
        }
    }

    @Override
    public Optional<Cashier> findById(final long id) {
        return Optional.ofNullable(cashiersMap.get(id));
    }

    @Override
    public synchronized Cashier save(final Cashier cashier) {
        if (cashier.getId() == 0) {
            cashier.setId(nextId.getAndIncrement());
        }
        cashiersMap.put(cashier.getId(), cashier);

        return persistCashier(cashier);
    }

    private void loadCashiers() throws IOException {
        if (!Files.exists(cashiersFilePath)) return;

        try (Stream<String> lines = Files.lines(cashiersFilePath)) {
            lines.map(cashierSerializer::parse)
                    .filter(Objects::nonNull)
                    .forEach(cashier -> {
                        cashiersMap.put(cashier.getId(), cashier);
                        nextId.updateAndGet(current -> Math.max(current, cashier.getId() + 1));
                    });
        }
    }

    private void initializeDefaultCashiersIfEmpty() {
        if (!cashiersMap.isEmpty()) return;

        final List<String> defaultCashiers = List.of("MARTINA", "PETER", "LINDA");
        final Balance defaultBalanceBgn = new Balance(BigDecimal.valueOf(1000),
                Map.of(TEN_LEVA, 50, FIFTY_LEVA, 10));
        final Balance defaultBalanceEur = new Balance(BigDecimal.valueOf(2000),
                Map.of(TEN_EUROS, 100, FIFTY_EUROS, 20));

        log.info("Initializing default cashiers");
        defaultCashiers.forEach(name -> {
            long id = nextId.getAndIncrement();
            Cashier cashier = new Cashier(id, name, Map.of(Currency.BGN, defaultBalanceBgn, Currency.EUR, defaultBalanceEur));
            cashiersMap.put(id, cashier);
        });
        persistAllCashiers();
    }

    private Cashier persistCashier(final Cashier cashier) {
        try {
            Files.writeString(
                    cashiersFilePath,
                    cashierSerializer.serialize(cashier) + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to persist cashiers", e);
        }
        return cashier;
    }

    private void persistAllCashiers() {
        try {
            Files.write(
                    cashiersFilePath,
                    cashiersMap.values().stream()
                            .map(cashierSerializer::serialize)
                            .toList(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to persist cashiers", e);
        }
    }

    private void createDirectoryIfMissing() {
        Path path = Path.of(cashierFileDirectory);
        if(!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            }catch (IOException e) {
                throw new UncheckedIOException("Failed to create directory", e);
            }
        }
    }
}