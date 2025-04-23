package org.myapp.cashdesk.controller;

import lombok.RequiredArgsConstructor;
import org.myapp.cashdesk.service.CashOperationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CashOperationController {

    private final CashOperationService cashOperationService;

    @PostMapping("/cash-operation")
    public String cashOperation() {
        return null;
    }
}
