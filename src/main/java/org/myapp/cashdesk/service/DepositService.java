package org.myapp.cashdesk.service;

import org.myapp.cashdesk.dto.request.CashOperationRequestDTO;
import org.myapp.cashdesk.model.transaction.Transaction;

/**
 * Interface contains all methods needed for processing deposit operations
 */
public interface DepositService {
    Transaction processDeposit(CashOperationRequestDTO request);
}
