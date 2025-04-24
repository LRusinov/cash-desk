package org.myapp.cashdesk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.exception.EntityNotFoundException;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.repository.CashierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CashierService {

    private final CashierRepository cashierRepository;

    public Cashier save(Cashier cashier) {
        return cashierRepository.save(cashier);
    }

    public Cashier findCashier(final Long id) {
        return cashierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Cashier with id=%d not found!", id)));
    }
}
