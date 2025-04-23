package org.myapp.cashdesk.repository;

import org.myapp.cashdesk.model.Cashier;

import java.util.List;
import java.util.Optional;

public interface CashierRepository {
    Optional<Cashier> findById(long id);

    Cashier save(Cashier cashier);

    List<Cashier> findAll();
}
