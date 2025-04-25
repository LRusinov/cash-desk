package org.myapp.cashdesk.service;

import org.myapp.cashdesk.dto.request.CashOperationRequestDTO;
import org.myapp.cashdesk.dto.response.TransactionDTO;

/**
 * Interface contains all methods needed for processing cash desk operation
 */
public interface CashDeskOperationService {
    TransactionDTO processOperation(CashOperationRequestDTO request);
}
