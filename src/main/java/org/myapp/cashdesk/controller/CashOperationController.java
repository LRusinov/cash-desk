package org.myapp.cashdesk.controller;

import lombok.RequiredArgsConstructor;
import org.myapp.cashdesk.dto.CashOperationRequestDTO;
import org.myapp.cashdesk.dto.TransactionDTO;
import org.myapp.cashdesk.service.CashDeskOperationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CashOperationController {

    private final CashDeskOperationService cashDeskOperationService;

    @PostMapping("/cash-operation")
    public TransactionDTO cashOperation(@RequestBody final CashOperationRequestDTO request) {
        return cashDeskOperationService.processOperation(request);
    }
}
