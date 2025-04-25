package org.myapp.cashdesk.service;

import org.myapp.cashdesk.dto.request.CashOperationRequestDTO;
import org.myapp.cashdesk.model.transaction.Transaction;

/**
 * Interface contains all methods needed for processing withdrawal operations
 */
public interface WithdrawalService {
    Transaction processWithdrawal(CashOperationRequestDTO request);
}
