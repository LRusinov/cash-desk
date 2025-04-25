package org.myapp.cashdesk.service;

import org.myapp.cashdesk.dto.request.CashOperationRequestDTO;
import org.myapp.cashdesk.dto.response.TransactionDTO;

public interface CashDeskOperationService {
    TransactionDTO processOperation(CashOperationRequestDTO request);
}
