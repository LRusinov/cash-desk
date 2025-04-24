package org.myapp.cashdesk.service;

import org.myapp.cashdesk.dto.request.CashOperationRequestDTO;
import org.myapp.cashdesk.model.transaction.Transaction;

public interface WithdrawalService {
    Transaction processWithdrawal(CashOperationRequestDTO request);
}
