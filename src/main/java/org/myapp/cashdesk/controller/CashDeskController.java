package org.myapp.cashdesk.controller;

import lombok.RequiredArgsConstructor;
import org.myapp.cashdesk.dto.CashBalanceResponseDTO;
import org.myapp.cashdesk.service.CashBalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CashDeskController {

    private final CashBalanceService cashBalanceService;

    @GetMapping("/cash-balance")
    public ResponseEntity<CashBalanceResponseDTO> cashBalance() {
        return null;
    }
}
