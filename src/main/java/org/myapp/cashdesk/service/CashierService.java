package org.myapp.cashdesk.service;

import org.myapp.cashdesk.model.cashier.Cashier;

public interface CashierService {
    Cashier save(Cashier cashier);
    Cashier findCashier(Long id);
}
