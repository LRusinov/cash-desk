package org.myapp.cashdesk.service;

import org.myapp.cashdesk.model.cashier.Cashier;

/**
 * Interface contain all need method for managing cashier.
 */
public interface CashierService {
    Cashier save(Cashier cashier);
    Cashier findCashier(long id);
}
