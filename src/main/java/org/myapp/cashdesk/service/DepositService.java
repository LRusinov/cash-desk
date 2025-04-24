package org.myapp.cashdesk.service;

import org.myapp.cashdesk.dto.request.CashOperationRequestDTO;
import org.myapp.cashdesk.model.transaction.Transaction;

public interface DepositService {
    Transaction processDeposit(CashOperationRequestDTO request);
}
