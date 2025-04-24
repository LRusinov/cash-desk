package org.myapp.cashdesk.controller;

import lombok.RequiredArgsConstructor;
import org.myapp.cashdesk.dto.CashierHistoryDTO;
import org.myapp.cashdesk.service.CashBalanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CashDeskController {

    private final CashBalanceService cashBalanceService;

    @GetMapping("/cash-balance")
    public ResponseEntity<List<CashierHistoryDTO>> cashBalance(@RequestParam(required = false) String cashierName,
                                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        return new ResponseEntity<>(cashBalanceService.getCashierBalance(cashierName, dateFrom, dateTo), HttpStatus.OK);
    }
}
