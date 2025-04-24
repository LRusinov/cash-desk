package org.myapp.cashdesk.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.exception.EntityNotFoundException;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.repository.CashierRepository;
import org.myapp.cashdesk.service.CashierService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CashierServiceV1 implements CashierService {

    private final CashierRepository cashierRepository;

    @Override
    public Cashier save(final Cashier cashier) {
        return cashierRepository.save(cashier);
    }

    @Override
    public Cashier findCashier(final Long id) {
        return cashierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Cashier with id=%d not found!", id)));
    }
}
