package org.myapp.cashdesk.repository;

import org.myapp.cashdesk.model.cashier.Cashier;

import java.util.Optional;

public interface CashierRepository {
    Optional<Cashier> findById(long id);

    Cashier save(Cashier cashier);
}
