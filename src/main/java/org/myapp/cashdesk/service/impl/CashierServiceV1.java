package org.myapp.cashdesk.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myapp.cashdesk.exception.EntityNotFoundException;
import org.myapp.cashdesk.model.cashier.Cashier;
import org.myapp.cashdesk.repository.CashierRepository;
import org.myapp.cashdesk.service.CashierService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for managing cashiers.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CashierServiceV1 implements CashierService {

    private final CashierRepository cashierRepository;

    /**
     * Saves given cashier.
     *
     * @param cashier cashier to be saved.
     * @return the saved cashier
     */
    @Override
    public Cashier save(final Cashier cashier) {
        return cashierRepository.save(cashier);
    }

    /**
     * Finds cashier by given id.
     *
     * @param id unique identifier of the cashier
     * @return the found cashier
     * @throws EntityNotFoundException if cashier with given id was not found
     */
    @Override
    public Cashier findCashier(final long id) {
        return cashierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Cashier with id=%d not found!", id)));
    }
}
