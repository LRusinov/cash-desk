package org.myapp.cashdesk.repository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.model.*;
import org.myapp.cashdesk.model.Currency;
import org.myapp.cashdesk.repository.serializer.CashierSerializer;
import org.myapp.cashdesk.repository.serializer.FileSerializer;
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

import static org.myapp.cashdesk.model.BgnDenomination.FIFTY_LEVA;
import static org.myapp.cashdesk.model.BgnDenomination.TEN_LEVA;
import static org.myapp.cashdesk.model.EurDenomination.ONE_HUNDRED_EUROS;
import static org.myapp.cashdesk.model.EurDenomination.TWENTY_EUROS;

@Slf4j
@Repository
public class FileCashierRepository implements CashierRepository {
    private static final long INITIAL_ID = 1L;

    private final FileSerializer<Cashier> cashierSerializer = new CashierSerializer();
    private final Map<Long, Cashier> cashiersMap = new ConcurrentHashMap<>();
    private final Path cashiersFile = Path.of("cashiers.txt");
    private final AtomicLong nextId = new AtomicLong(INITIAL_ID);

    @PostConstruct
    public void init() {
        try {
            loadCashiers();
            initializeDefaultCashiersIfEmpty();
            log.info("Initialized with {} cashiers", cashiersMap.size());
        } catch (Exception e) {
            log.error("Initialization failed", e);
            throw new UncheckedIOException(new IOException("Repository initialization failed", e));
        }
    }

    @Override
    public List<Cashier> findAll() {
        return new ArrayList<>(cashiersMap.values());
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
        persistAllCashiers();
        return cashier;
    }

    private void loadCashiers() throws IOException {
        if (!Files.exists(cashiersFile)) return;

        try (Stream<String> lines = Files.lines(cashiersFile)) {
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
        final CurrencyBalance defaultBalanceBgn = new CurrencyBalance(Currency.BGN,
                Map.of(FIFTY_LEVA, 10, TEN_LEVA, 50), BigDecimal.valueOf(1000));
        final CurrencyBalance defaultBalanceEur = new CurrencyBalance(Currency.EUR,
                Map.of(ONE_HUNDRED_EUROS, 10, TWENTY_EUROS, 50), BigDecimal.valueOf(2000));

        log.info("Initializing default cashiers");
        defaultCashiers.forEach(name -> {
            long id = nextId.getAndIncrement();
            Cashier cashier = new Cashier(id, name, defaultBalanceBgn, defaultBalanceEur);
            cashiersMap.put(id, cashier);
        });
        persistAllCashiers();
    }

    private void persistAllCashiers() {
        try {
            Files.write(
                    cashiersFile,
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
}